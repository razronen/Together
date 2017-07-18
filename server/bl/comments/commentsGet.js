var mongoose = require('mongoose');
var post = mongoose.model('post');
var comment = mongoose.model('comment');

/**
 * Getting the last comments by amount of this post(postID)
 * @param postID - id of post containging the comments.
 * @param amount - amount of comments wanted.
 * @param next - route function.
 */
exports.getComments = function(postID,amount, next){
    post.find({id: postID}, function(err,data){
        if(err) next(err,data);
        else {
            if(data == undefined || data[0] == undefined || data[0].comments == undefined){
                next(err,data);
            } else {
                //get the amount of last comments wanted:
                var comments_ids;
                var comments = data[0].comments;
                if (comments.length > amount) {
                    comments_ids = comments.slice(0,amount);
                } else {
                    comments_ids = comments;
                }
                pullComment(comments_ids, [],pullComment,function(err,data){
                    console.log("result:" + data.toString());
                    next(err,data);
                });
            }
        }
    });
}

/**
 * pulling specific comments.
 * @param comments_ids - their ids.
 * @param result
 * @param next - callback function.
 * @param final final function callback.
 */
var pullComment = function(comments_ids, result, next,final){
    if(comments_ids.length==0) final(null,result);
    else{
        comment.find({id: comments_ids[0].comment_id}, function(err,data){
            if(err) final(err,null);
            else{
                result.push(data[0])
                next(comments_ids.slice(1),result,pullComment,final);
            }
        })
    }
}

/**
 * get all comments of this post.
 * @param next - route function.
 */
exports.getAllComments = function(next){
    comment.find(function(err, data){
        if(err) next(err);
        next(err, data);
    })
}