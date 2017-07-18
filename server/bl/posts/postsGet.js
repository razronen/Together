/**
 * Created by Raz on 1/28/2017.
 */
var mongoose = require('mongoose');
var post = mongoose.model('post');
var queue = mongoose.model('post_queue');
var user = mongoose.model('user');

/**
 * Get last posts. by amount and by the last time submitted
 * @param userID - of request author
 * @param lastTime - from when to pull posts. -1:now.
 * @param amount - amount of posts.
 * @param next - route function.
 */
exports.getPosts = function(userID, lastTime,amount, next){
    var threshold = (lastTime==-1)?9999999999999:lastTime;
    user.find({id: userID}, function(err, ent){
        if(err) next(err);
        else{
            var filter;
            if(ent == undefined || ent.length==0) next("");
            else {
                if (ent[0].entity == 'Child') {
                    filter = {$or: [{is_public: true}, {publisherID: userID}]}
                } else {
                    filter = {};
                }
                post.find({$and: [filter, {date: {$lt: threshold}}]})
                    .sort('date')
                    .exec(function (err, data) {
                        if (err) next(err)
                        else next(err, data.slice(Math.max(data.length - amount, 0)));
                    });
            }
        }
    })
}

/**
 * get specific post by an id.
 * @param postID - id of post.
 * @param next - callback function.
 */
exports.getSpecificPost = function(postID, next){
    post.find({id: postID}, function(err,data){
        if(err) next(err);
        else next(err,data);
    });
}

/**
 * Search post by a specific string. check equals for title and content.
 * @param userID - of request author
 * @param string - search string
 * @param amount - amount of max post that return.
 * @param next - callback function.
 */
exports.searchPosts = function(userID, string,amount, next){
    post.find({ $and: [  { $or: [{ is_public: true}, { publisherID: userID}] }
                         ,{ $or: [{ title: { $regex: ".*" + string + ".*" }}
                                  ,{ message: { $regex: ".*" + string + ".*" }}]}]})
        .sort('date')
        .exec(function(err,data){
            if(err) next(err)
            else next(err, data.slice(Math.max(data.length-amount,0)));
        });
}

/**
 * Get all posts.
 * @param next - route function.
 */
exports.getAllPosts = function(next){
    post.find(function(err, data){
        if(err) next(err);
        next(err, data);
    })
}

/**
 * Get the queue post - post that are not answered yet by a psychologist.
 * @param next - route function.
 */
exports.getPostsQueue = function(next){
    queue.findOne({id: "1"},function(err, data){
        if(err) next(err)
        else {
            if(data==undefined){
                next(err,undefined);
            } else {
                next(err,data.posts);
            }
        }
    })
}


