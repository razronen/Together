var models = ['post.js'
    ,'comment.js'
    ,'post_queue.js'
    ,'user.js'
    ,'response.js'
    ,'chat_queue'
    ,'chat_conversation'
    ,'pending_shifts'
    ,'quote'
    ,'help_request'
    ,'time_statistics'
    ,'update'
    ,'potential_user'];
/**
 * This function requires all the differenet models.
 */
exports.init =function(){
    var l = models.length;
    for (var i=0;i<l;i++){
        var t = './' + models[i];
        require(t);console.log(t);
    }

};
