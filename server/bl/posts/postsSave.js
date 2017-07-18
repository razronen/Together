var mongoose = require('mongoose');
var post = mongoose.model('post');
var postQueue = mongoose.model('post_queue');
var rand = require("generate-key");
var queue = mongoose.model('post_queue');
var userModel = mongoose.model('user');
var time_statistics = mongoose.model('time_statistics');

/**
 * Save post to db
 * save post to time statistics(day statistics)
 * save post to user statistics.
 * push to posts unanswered queue.
 * @param reqPost - post object
 * @param next - route function.
 */
exports.savePosts = function(reqPost, next) {
    reqPost.id = rand.generateKey().toString();
    var newPost = new post(reqPost);
    newPost.save(function(err, data){
        if(err) next(err, data);
        else {
            saveToTimeStatistics(reqPost.is_public,reqPost.author_entity,reqPost.author_id,reqPost.author_name);
            savePostToUserStatistics(reqPost,function(err,data1){
                if(err) next(err,data);
                else {
                    if (reqPost.publisher_entity == "Child") {
                        pushToQueue(reqPost.id, reqPost.date, reqPost.publisher, function () {
                            next(err, data);
                        })
                    } else {
                        next(err, data);
                    }
                }
            });
        }
    });
};

/**
 * Save information to day statistics(time statistics).
 * @param is_public - private/public
 * @param entity - of author
 * @param _id  - of author
 * @param _name - of author
 */
var saveToTimeStatistics = function(is_public, entity, _id, _name){
    var p = undefined;
    if(is_public=='true'){
        p = {public_post: (new Date).getTime()}
    } else {
        p = {private_post: (new Date).getTime()}
    }
    var post_statistics = new time_statistics(p);
    post_statistics.save();
    var action = undefined;
    if(entity=='Child'){
        action = {child_action: {time : (new Date).getTime(), name: _name, id: _id}}
    } else{
        action = {psychologist_action: {time : (new Date).getTime(), name: _name, id: _id}}
    }
    var action_statistics = new time_statistics(action);
    action_statistics.save();
}

/**
 * Save post to user statistics
 * @param reqPost - post object
 * @param next - callback function.
 */
var savePostToUserStatistics = function(reqPost, next){
    var conditions = {id: reqPost.publisherID}
        ,options = {multi: true};
    var update = undefined;
    if(reqPost.is_public=='true'){
        update = {$push: {"posts_publised": {   post_title: reqPost.title,
            post_msg: reqPost.message  }
        },  $inc : {public_posts_published: 1 }};
    } else {
        update = {$push: {"posts_publised": {   post_title: reqPost.title,
            post_msg: reqPost.message  }
        },  $inc : {private_posts_published: 1 }};
    };
    userModel.update(conditions, update, options, function (err, numAffected) {
        if (err) next(err, numAffected);
        else next(err, numAffected);
    });
}

/**
 * push to unanswer post queue.
 * @param _id - of author
 * @param _time - time of init post
 * @param _author- author name
 * @param next - callback function.
 */
var pushToQueue = function(_id,_time,_author, next){
    queue.find({id: "1"},function(err, data){
        if(err) next(err);
        else addPostToQueue(_id,_time,_author, next);
    })
}

/**
 * Actually adding the post to queue.
 * @param _id - of author
 * @param _time - of init post
 * @param _author - name
 * @param next - callback function.
 */
var addPostToQueue = function (_id,_time,_author,next){
    var conditions = {id: "1"}
        , update = {$push: { posts: {post_id: _id, time: _time, author: _author}}}
        , options = {multi: true};
    postQueue.update(conditions, update, options, function (err, data) {
        next();
    });
}