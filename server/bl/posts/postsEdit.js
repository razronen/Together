var mongoose = require('mongoose');
var postModel = mongoose.model('post');

/**
 * Editing title and content information of message.
 * @param postID - post id.
 * @param post - post object
 * @param next - route function.
 */
exports.editPost = function(postID, post, next) {
    var conditions = { id: postID }
        , update = { message: post.message, title: post.title }
        , options = { multi: true };

    postModel.update(conditions, update, options, function(err, affected, resp){
        if(err) {
            next(err);
        }
        else {
            post.id = postID;
            next(err,post);
        }
    });
}