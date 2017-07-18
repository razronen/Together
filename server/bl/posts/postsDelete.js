var mongoose = require('mongoose');
var postModel = mongoose.model('post');
var commentModel = mongoose.model('comment');
var userModel = mongoose.model('user');

/**
 * Deleting a post
 * Deleting all its comments
 * Editing time statistics and user statistics.
 * @param postID - id of post.
 * @param next - callback function.
 */
exports.deletePost = function(postID, next) {
    postModel.findOne({ 'id' : postID}, 'comments', function(err, item){
        if(err) next(err,item);
        else {
            editUserDelStatistics(postID, function(err,data){
                if(err) next(err,data);
                else {
                    if(item == undefined) {
                        postModel.find().remove({'id': postID}).remove(function (err, data) {
                            if (err) next(err);
                            else
                                next(err, postID);
                        });
                    }
                    else {
                        var comments = item.comments;
                        delComments(comments, delComments, function () {
                            postModel.find().remove({'id': postID}).remove(function (err, data) {
                                if (err) next(err);
                                else
                                    next(err, postID);
                            });
                        });
                    }
                }
            })
        }
    })
}

/**
 * Editing user statistics - a post was deleted.
 * @param _id - of user.
 * @param next - callback function.
 */
var editUserDelStatistics = function(_id, next){
    postModel.findOne({id: _id},function(err,data){
        if(err) next(err,data);
        else {
            var conditions = {id: data.publisherID}
                ,options = {multi: true}
                ,update = {$inc : {post_erased_by_manager: 1 }};
            userModel.update(conditions, update, options, function (err, numAffected) {
                if (err) next(err, numAffected);
                else next(err, numAffected);
            });
        }
    })
}

/**
 * Helper function for deletin each comment of this posts
 * @param comments - comments subobject of post.
 * @param next - callback function.
 * @param final - callback function.
 */
var delComments = function(comments,next,final){
    if(comments[0] == undefined || comments.length==0) final();
    else {
        var sliced = comments.slice(1);
        commentModel.find().remove({id: comments[0].comment_id}).remove(function (err, data) {
            if (sliced.length > 0)
                next(sliced, next, final);
            else
                final();
        });
    }
}
