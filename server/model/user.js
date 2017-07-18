var mongoose = require('mongoose');


var Schema = mongoose.Schema;

/**
 * Present the user model.
 * @type {mongoose.Schema}
 */
var userSchema = new Schema({

    id: {type: String},

    entity: {type: String},

    name: {type: String},

    icon: {type: String},

    deviceId: {type: String},

    email: {type: String},

    pass: {type: String},

    //statistics:

    lastlogin: {type: String},

    public_posts_published: {type: Number, default: 0},

    private_posts_published: {type: Number, default: 0},

    /**
     * Small representation of the posts this user created.
     */
    posts_publised: [{
        post_title: {type: String},
        post_msg: {type: String}
    }],

    /**
     * Small representation of the comments this user created.
     */
    comments_published: [{
        post_title: {type: String},
        comment_msg: {type: String}
    }],

    immediate_help_messages: [{
        msg : {type: String}
    }],

    //if help request answered or not
    immediate_help_messages_answered:  {type: Number, default: 0},

    immediate_help_messages_not_answered:  {type: Number, default: 0},

    //avg_login_time_per_day:  {type: String},

    post_erased_by_manager: {type: Number, default: 0},

    /**
     * Saves all the responses coommunicated between him and his peers.
     */
    conversations: [{
        peer: {type: String},
        msg: {type: String}
    }],

    //missed_post_by_psycho: {type: Number}

});

mongoose.model('user', userSchema);