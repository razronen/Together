var mongoose = require('mongoose');
var validators = require('mongoose-validators');


var Schema = mongoose.Schema;

/**
 * This model represents the comment representation.
 * @type {mongoose.Schema}
 */
var commentSchema = new Schema({

    message: {type: String},

    publisher: {type: String},

    publisherID: {type: String},

    publisher_entity: {type: String},

    date: {type: String},

    postID: {type: String},

    id: {type: String},

    image: {type: String},

    image_x: {type: String},

    image_y: {type: String},

    link: {type: String},

});

mongoose.model('comment', commentSchema);