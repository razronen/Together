var mongoose = require('mongoose');
var post = mongoose.model('post');
var commentModel = mongoose.model('comment');

/**
 * Editing the message of this comment.
 * @param id - comment id.
 * @param comment - comment object (containing message content).
 * @param next - route function.
 */
exports.editComment = function(id,comment, next) {
    var conditions = { id: id }
        , update = { message: comment.message}
        , options = { multi: true };

    commentModel.update(conditions, update, options, function(err, doc){
        if(err) next(err);
        else {
            next(err,comment);
        }
    });
}
