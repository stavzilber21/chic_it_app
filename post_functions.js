import { initializeApp } from 'firebase/app';
import { getFirestore, collection, query, where, setDoc , doc, getDoc, deleteDoc } from 'firebase/firestore';
import { FieldValue } from 'firebase/firestore';
import "firebase/auth"
import admin from 'firebase-admin';
import serviceAccount from './service_account.json' assert { type: "json" };
import * as fb from 'firebase/firestore'
import 'firebase/firestore'

const firebaseConfig = {
  apiKey: "AIzaSyBrcpckvsh7oBhIn0q1rt2sPlDY7kKwtEM",
  authDomain: "chicit-a5e00.firebaseapp.com",
  databaseURL: "https://chicit-a5e00-default-rtdb.firebaseio.com",
  projectId: "chicit-a5e00",
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);
//const usersCollection = fb.collection(db, 'users');

const usersCollection = collection(db, 'users');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://chicit-a5e00-default-rtdb.firebaseio.com'
});

export async function deletePost(pid, publisher, uid) {
  const postRef = fb.doc(db, 'posts', pid)
  const postSnapshot = await fb.getDoc(postRef);
  if (postSnapshot.exists() && postSnapshot.data().publisher === uid) {
    await fb.deleteDoc(postRef);    
    console.log('Post deleted successfully.');
  } else {
    console.log('Post not deleted. Publisher does not match current user.');
  }
  return "done"
}

export async function editProfile(uid, gender, size, username, fullname, imageurl) {
  try {
    await fb.updateDoc(fb.doc(db, "usersById", uid), 
    {
      username: username,
      fullname: fullname, 
      gender: gender,
      size: size,
      imageurl: imageurl
    });    
    console.log(`user ${username} updated successfully.`);
  } catch (error) {
    console.error('Error updating user:', error);
  }
  return "done"
}

export async function addUser(username, fullName, email, phone, password) {
  try {
    const authUser = await admin.auth().createUser({ email, password });
    await fb.setDoc(fb.doc(db, "usersById", authUser.uid), 
    {
      uid: authUser.uid,
      email: email,
      username: username,
      fullname: fullName || '', // assign an empty string if fullName is undefined
      phone: phone || '', // assign an empty string if phone is undefined
      postCount: 0,
    });    
    console.log(`New user ${username} created successfully.`);
  } catch (error) {
    console.error('Error creating new user:', error);
  }
  return "done"
}

export async function checkIfEmailExists(email) {
  try {
    const q = query(usersCollection, where('email', '==', email));
    const ans = await getDocs(q);
    if (ans.docs.length === 0 ) {
      // do something
    }
  } catch (error) {
    console.error('Error checking if email exists:', error);
  }
}

export async function makePost(imageUrl, description, store, price, type, uid) {
  const userRef = fb.doc(db, 'usersById', uid)
  const userDoc = await fb.getDoc(userRef)
  if(userDoc.exists()){
      const numPosts = userDoc.data().postCount + 1
      await fb.updateDoc(userRef, {postCount: numPosts})
  }
  else{
      console.log("error in make post")
  }
  try {
    const docRef = await fb.addDoc(fb.collection(db, "posts"), 
    {
      imageurl: imageUrl,
      description: description,
      store: store,
      price: price, 
      type: type, 
      publisher: uid,
      postid: ''
    });
    const docKey = docRef.id;
    await fb.updateDoc(docRef, {postid: docKey})
  } catch (error) {
    console.error('Error post:', error);
  }
  return "done"
}



// export async function savePost(uid, pid) {
//   try {
//     const userRef = fb.doc(db, 'saves', uid);
//     const userSnapshot = await fb.getDoc(userRef);
//     const userData = userSnapshot.exists() ? userSnapshot.data() : {};
    
//     if (userData.hasOwnProperty(pid)) {
//       delete userData[pid];
//       await fb.setDoc(userRef, userData);
//       console.log("Post removed successfully!");
//     } else {
//       userData[pid] = true;
//       await fb.setDoc(userRef, userData);
//       console.log("Post saved successfully!");
//     }
//   } catch (error) {
//     console.error("Error saving/removing post: ", error);
//   }
  
//   return "done";
// }

export async function savePost(uid, pid) {
  try {
    const userRef = fb.doc(db, 'saves', uid)
    const userSnapshot = await fb.getDoc(userRef);
    if (userSnapshot.exists() && userSnapshot.data().hasOwnProperty([pid])) {
      await fb.deleteDoc(userRef);
      console.log("Post removed successfully!");
    } else {
      await fb.setDoc(fb.doc(db, "saves", uid), {[pid]: true});  
      console.log("Post saved successfully!");
    }
  } catch (error) {
    console.error("Error saving/removing post: ", error);
  }
  return "done"
}



export async function followUser(uid, pid) {
  try {
    const userRef = fb.doc(db, 'follows', uid);
    const userSnapshot = await fb.getDoc(userRef);

    if (userSnapshot.exists()) {
      const userDocData = userSnapshot.data();
      const followers = userDocData.followers || {};
      const following = userDocData.following || {};

      if (following.hasOwnProperty(pid)) {
        // If the pid exists in following, remove it
        delete following[pid];
        console.log("Unfollowed successfully!");

        // Remove the follower uid from the followed user's document
        const followedUserRef = fb.doc(db, 'follows', pid);
        await fb.updateDoc(followedUserRef, { [`followers.${uid}`]: fb.deleteField() });
      } else {
        // If the pid doesn't exist in following, add it
        following[pid] = true;
        console.log("Followed successfully!");

        // Update the followers hashmap in the followed user's document
        const followedUserRef = fb.doc(db, 'follows', pid);
        const followedUserSnapshot = await fb.getDoc(followedUserRef);
        if (followedUserSnapshot.exists()) {
          const followedUserDocData = followedUserSnapshot.data();
          const followedUserFollowers = followedUserDocData.followers || {};
          followedUserFollowers[uid] = true; // Add the follower uid to the followers hashmap
          await fb.setDoc(followedUserRef, { followers: followedUserFollowers });
        } else {
          await fb.setDoc(followedUserRef, { followers: { [uid]: true } });
        }
      }

      // Update the user document with the modified followers and following hashmaps
      await fb.updateDoc(userRef, { followers, following });
    } else {
      // If the user document doesn't exist, create it with the initial follower and following
      await fb.setDoc(userRef, { followers: {}, following: { [pid]: true } });
      console.log("Followed successfully!");

      // Update the followers hashmap in the followed user's document
      const followedUserRef = fb.doc(db, 'follows', pid);
      const followedUserSnapshot = await fb.getDoc(followedUserRef);
      if (followedUserSnapshot.exists()) {
        const followedUserDocData = followedUserSnapshot.data();
        const followedUserFollowers = followedUserDocData.followers || {};
        followedUserFollowers[uid] = true; // Add the follower uid to the followers hashmap
        await fb.setDoc(followedUserRef, { followers: followedUserFollowers });
      } else {
        await fb.setDoc(followedUserRef, { followers: { [uid]: true } });
      }
    }
  } catch (error) {
    console.error("Error following/unfollowing user: ", error);
  }

  return "done";
}


// export async function addPost(imageUrl, uid, items,type) {
//   const hashMap = JSON.parse(items);
//   const userRef = fb.doc(db, 'usersById', uid)
//   const userDoc = await fb.getDoc(userRef)
//   if(userDoc.exists()){
//       const numPosts = userDoc.data().postCount + 1
//       await fb.updateDoc(userRef, {postCount: numPosts})
//   }
//   else{
//       console.log("error in make post")
//   }
//   try {
//     const docRef = await fb.addDoc(fb.collection(db, "posts"), 
//     {
//       imageurl: imageUrl,
//       items: hashMap, // Add the items dictionary to the post document
//       publisher: uid,
//       postid: '',
//       type: type
//     });
//     const docKey = docRef.id;
//     await fb.updateDoc(docRef, {postid: docKey})
//   } catch (error) {
//     console.error('Error post:', error);
//   }
//   return "done"
// }

export async function addPost(imageUrl, uid, items, type) {
  const hashMap = JSON.parse(items);
  const userRef = fb.doc(db, 'usersById', uid);
  const userDoc = await fb.getDoc(userRef);
  if (userDoc.exists()) {
    const numPosts = userDoc.data().postCount + 1;
    await fb.updateDoc(userRef, { postCount: numPosts });
  } else {
    console.log("Error in creating post");
  }
  
  try {
    const postRef = fb.collection(db, "posts");
    const postSnapshot = await fb.getDocs(postRef);
    const numItems = Object.keys(hashMap).length;
    
    const newPostData = {
      imageurl: imageUrl,
      items: {}, // Initialize an empty object for the modified items
      publisher: uid,
      postid: "",
      type: type
    };
    
    // Assign sequential IDs to items
    Object.entries(hashMap).forEach(([key, value], index) => {
      newPostData.items[index + 1] = value;
    });

    const newPostRef = await fb.addDoc(postRef, newPostData);
    const newPostId = newPostRef.id;
    await fb.updateDoc(newPostRef, { postid: newPostId });

    return "done";
  } catch (error) {
    console.error("Error creating post:", error);
    throw error;
  }
}
