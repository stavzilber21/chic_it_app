import express from 'express';
import * as get_functions from "../model/get_functions.js"

const getRouter = express.Router()

//for showing user details in profilefragment
getRouter.get('/getUserDetails', async(req, res) =>{
    const user = await get_functions.getUser(req.query.uid)
    //console.log(user)
    if (user.length === 0){
      return res.status(404).send()
    }
    return res.status(200).send(user)
  })
  
getRouter.get('/getPostDetails', async(req, res) =>{
    const post = await get_functions.getPostDetails(req.query.pid)
    //console.log(post)
    if (post.length === 0){
      return res.status(404).send()
    }
    return res.status(200).send(post)
  })

//count posts of each user
getRouter.get('/countPost', async(req, res) =>{
    const countPosts = await get_functions.countPost(req.query.uid)
    //console.log(countPosts)
    if (countPosts.length === 0){
      return res.status(404).send()
    }
    res.status(200).send(countPosts.toString());
})

//count followers and following of each user
getRouter.get('/getFollowersAndFollowingCount', async(req, res) =>{
  const countFollow = await get_functions.getFollowersAndFollowingCount(req.query.uid)
  console.log(countFollow.toString())
  if (countFollow.length === 0){
    return res.status(404).send()
  }
  res.status(200).send(countFollow.toString());
})

getRouter.get('/check', async(req, res) =>{
    const isSaved = await get_functions.check(req.query.uid, req.query.pid)
    if (isSaved.length === 0){
      return res.status(404).send()
    }
    res.status(200).send(isSaved);
})

getRouter.get('/checkFollows', async(req, res) =>{
  const isFollow = await get_functions.checkFollows(req.query.uid, req.query.pid)
  if (isFollow.length === 0){
    return res.status(404).send()
  }
  res.status(200).send(isFollow);
})

//get all the categories
getRouter.get('/getMyPhoto', async(req, res) =>{
    const postsList = await get_functions.getMyPhoto(req.query.uid)
    //console.log(postsList)
    if (postsList.length === 0){
      return res.status(404).send()
    }
    return res.status(200).send(postsList)
})

getRouter.get('/mySavedPosts', async(req, res) =>{
    const postsList = await get_functions.mySavedPosts(req.query.uid)
    // console.log(postsList)
    if (postsList.length === 0){
      return res.status(404).send()
    }
    return res.status(200).send(postsList)
})

getRouter.get('/homePosts', async(req, res) =>{
    const postsList = await get_functions.homePosts()
    //console.log(postsList)
    if (postsList.length === 0){
      return res.status(404).send()
    }
    return res.status(200).send(postsList)
})

getRouter.get('/homeUsers', async(req, res) =>{
  const usersList = await get_functions.homeUsers()
  // console.log(usersList)
  if (usersList.length === 0){
    return res.status(404).send()
  }
  return res.status(200).send(usersList)
})


getRouter.get('/getFollowings', async(req, res) =>{
  const countFollow = await get_functions.getFollowings(req.query.uid)
  console.log(countFollow.toString())
  if (countFollow.length === 0){
    return res.status(404).send()
  }
  res.status(200).send(countFollow.toString());
})


getRouter.get('/getFollowers', async(req, res) =>{
  const countFollow = await get_functions.getFollowers(req.query.uid)
  console.log(countFollow.toString())
  if (countFollow.length === 0){
    return res.status(404).send()
  }
  res.status(200).send(countFollow.toString());
})

getRouter.get('/getPostItems', async(req, res) =>{
  const post = await get_functions.getPostItems(req.query.pid)
  // console.log(post)
  if (post.length === 0){
    return res.status(404).send()
  }
  return res.status(200).send(post)
})

export { getRouter }
