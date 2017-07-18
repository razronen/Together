var mongoose = require('mongoose');


var Schema = mongoose.Schema;

/**
 * This model represents the queue that holds ths posts that are not yet answered by psychologist.
 * @type {mongoose.Schema}
 */
var post_queueSchema = new Schema({

    id : {type: String},

    posts: [{
        post_id : {type: String},

        author : {type: String},

        time : {type: Number}
    }]

});

mongoose.model('post_queue', post_queueSchema);