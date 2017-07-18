var mongoose = require('mongoose');
var post = mongoose.model('post');
var comment = mongoose.model('comment');
var user = mongoose.model('user');
var queue = mongoose.model('post_queue');
var rand = require("generate-key");
var time_statistics = mongoose.model('time_statistics');

/**
 * Saving a comment.
 * saving it to time statistics
 * to user statistics
 * to post object
 * to queues
 * @param reqComment - the comment content and information.
 * @param next - route function.
 */
exports.saveComments = function(reqComment, next) {
    reqComment.id = rand.generateKey().toString();
    var newComment = new comment(reqComment);
    newComment.save(function(err, data1){
        if(err) next(err, data1);
        else {
            saveToTimeStatistics(reqComment);
            saveCommentToUserStatistics(reqComment);
            user.findOne({id: reqComment.publisherID}, function(err,data2){
                if(err || data2==undefined) next(err,data2);
                else {
                    var answered = false;
                    var update;
                    if(data2.entity == "Psychologist" || data2.entity == "Manager" || data2.entity == "Dev"){
                        answered = true;
                        update = {$push: {"comments": {comment_id: data1.id}}, answered: true};
                    } else {
                        update = {$push: {"comments": {comment_id: data1.id}}}
                    }
                    var conditions = {id: reqComment.postID}
                        , options = {multi: true};
                    post.update(conditions, update, options, function (err, numAffected) {
                        if (err) next(err, data1);
                        else {
                            if(answered){
                                //update queue.
                                var conditions = {id: "1"}
                                    , update = {$pull: { posts: {post_id: reqComment.postID}}}
                                    , options = {multi: true};
                                queue.update(conditions, update, options, function (err, data) {
                                    next(err,data1);
                                });
                            }
                            else{
                                next(err,data1);
                            }
                        }
                    });
                }
            });
        }
    });
}

/**
 * Saving comment was made to time statistics(day statistics).
 * @param reqComment - comment object.
 */
var saveToTimeStatistics = function(reqComment){
    var c = {response: (new Date).getTime()};
    var comment_statistics = new time_statistics(c);
    comment_statistics.save();
    var action = undefined;
    if(reqComment.publisher_entity=='Child'){
        action = {child_action: {time : (new Date).getTime(), name: reqComment.publisher, id: reqComment.publisherID}}
    } else{
        action = {psychologist_action: {time : (new Date).getTime(), name: reqComment.publisher, id: reqComment.publisherID}}
    }
    var action_statistics = new time_statistics(action);
    action_statistics.save();
}

/**
 * Saving comment was made to user statistics.
 * @param reqComment - comment object.
 */
var saveCommentToUserStatistics = function(reqComment){
    post.findOne({id: reqComment.postID}, function(err,data){
        if(err) next(err,data);
        else {
            var conditions = {id: reqComment.publisherID}
                ,options = {multi: true}
                ,update = { $push: {"comments_published": {   post_title: data.title,
                comment_msg: reqComment.message  }}};
            user.update(conditions, update, options, function (err, numAffected) {
            });
        }
    });
}
