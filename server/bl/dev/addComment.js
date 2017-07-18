var mongoose = require('mongoose');
var post = mongoose.model('post');
var commentModel = mongoose.model('comment');

/**
 * Saving comment and updating post.
 * @param _postID - post id
 * @param _message - content
 * @param _publisher - author of comment
 * @param _publisherID - author id of comment
 * @param _date - date the comment was made
 * @param _id - id of user.
 * @param next - route function
 */
exports.saveComment = function(_postID,_message,_publisher,_publisherID,_date,_id,next) {
    var c = { message: _message
        ,publisher: _publisher
        ,publisherID : _publisherID
        ,date: _date
        ,postID: _postID
        ,id: _id}
    var newComment = new commentModel(c);
    newComment.save(function(err, data){
        if(err) next(err, data);
        else {
            var conditions = { id: c.postID }
                , update = { $push: { "comments" : { comment_id: data.id } } }
                , options = { multi: true };
            post.update(conditions, update, options, function(err, numAffected){
                if(err) next(err, data);
                next(data);
            });
        }
    });
}