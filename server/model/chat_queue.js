var mongoose = require('mongoose');


var Schema = mongoose.Schema;

/**
 * This is the model that represents the queue that's hold the conversation.
 * @type {mongoose.Schema}
 */
var chat_queueSchema = new Schema({

    id : {type: String},

    obj: [{

        name: {type: String},

        user_id : {type: String},

        //child_id that in that conversation
        conversation_id : {type: String},

        time : {type: Number}
    }]

});

mongoose.model('chat_queue', chat_queueSchema);