var mongoose = require('mongoose');
var post = mongoose.model('post');

/**
 * Save a post.
 * @param _is_public - private/public
 * @param _title - title of post
 * @param _message - content of post
 * @param _publisher - name
 * @param _publisherID - id of author
 * @param _date - of post init
 * @param _id - of post
 * @param next - route function.
 */
exports.savePost = function(_is_public,_title,_message,_publisher,_publisherID,_date,_id, next) {
    var p = { is_public: _is_public
        ,title: _title
        ,message : _message
        ,publisher: _publisher
        ,publisherID: _publisherID
        ,date: _date
        ,id : _id}
    var newPost = new post(p);
    newPost.save(function(err, data){
        if(err) next(err, data);
        next(data);
    });
}