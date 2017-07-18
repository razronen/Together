var mongoose = require('mongoose');
var validators = require('mongoose-validators');


var Schema = mongoose.Schema;

/**
 * This model represntes the post entity.
 * @type {mongoose.Schema}
 */
var postSchema = new Schema({

    is_public: {type: Boolean},

    title: {type: String},

    message: {type: String},

    publisher: {type: String},

    publisherID: {type: String},

    publisher_entity: {type: String},

    publisher_icon: {type: String},

    date: {type: Number},

    id: {type: String},

    color: {type: Number},

    //by psychologist
    answered: {type:Boolean},

    image: {type: String},

    image_x: {type: String},

    image_y: {type: String},

    link: {type: String},

    //array of comments ID.
    comments: [{
        comment_id : {type: String}
    }]
});

mongoose.model('post', postSchema);