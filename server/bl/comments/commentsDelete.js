var mongoose = require('mongoose');
var postModel = mongoose.model('post');
var commentModel = mongoose.model('comment');

/**
 * Deleting aa comment.
 * Updating the post containing this comment that this comment got deleted.
 * @param commentID - comment ID to delete.
 * @param postID - the Post id containes this comment.
 * @param next - route function.
 */
exports.deleteComment = function(commentID, postID, next) {
    commentModel.find().remove({id: commentID}).remove(function(err, data){
        if(err) next(err)
        else{
            postModel.findOne({ 'id' : postID}, 'comments', function(err, item){
                var oldComments = item.comments;
                var newComments =  [];
                oldComments.forEach(function(comment){
                    if(comment.comment_id!=commentID){
                        newComments.push(comment);
                    }
                });
                var conditions = { id: postID }
                    , update = { comments: newComments }
                    , options = { multi: true };

                postModel.update(conditions, update, options, function(err, affected, resp){
                    if(err) {
                        next(err);
                    }
                    else {
                        next(err,{ post: postID, comment: commentID});
                    }
                });
            })
        }
    });

}
