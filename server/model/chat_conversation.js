var mongoose = require('mongoose');


var Schema = mongoose.Schema;
/**
 * This is the model that repsresnets a chat conversation.
 * psychos get pushed and pulled to this conversation.
 * @type {mongoose.Schema}
 */
var chat_conversation_collectionSchema = new Schema({

    child_id: {type: String},

    child_name: {type: String},

    child_status: {type: String},

    child_deviceId: {type: String},

    psycho: [{

        psycho_id: {type: String},

        psycho_name: {type: String},

        psycho_status: {type: String},

        psycho_deviceId: {type: String}
    }]


});

mongoose.model('chat_conversation', chat_conversation_collectionSchema);