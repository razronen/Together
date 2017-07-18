var mongoose = require('mongoose');
var S = require('string');
require('./model/firebase');
require('./model/init').init();
//DB CONNECTION
mongoose.connect('188.166.144.94:27017', function(){
//mongoose.connect('127.0.0.1:27017', function(){
  console.log('Connected to mongoose.');
  //mongoose.connection.db.dropDatabase();
  require('./model/initiate');
});
var path = require('path');
require('ssl-root-cas')
    .inject()
    .addFile(path.join(__dirname, './', 'my-private-root-ca.cert.pem'))
;

//REQUIREMENTS:
var path = require('path');
var bodyParser = require('body-parser');

var express = require('express');
var routeGet = require('./routes/routeGet');
var routeSave = require('./routes/routeSave');
var routeEdit = require('./routes/routeEdit');
var routeDelete = require('./routes/routeDelete');
var routeDev = require('./routes/routeDev');
var auth = require('./bl/auth/authenticate');
var cookieParser = require('cookie-parser');
var session = require('express-session');
var morgan = require('morgan');
var https = require('https');
var fs = require('fs');

var server = express();
server.set('view engine', 'html');
server.engine('html', require('ejs').renderFile);
server.use(morgan('dev'));
server.use(cookieParser());
server.use(session({secret: 'anystringoftext',
  saveUninitialized: true,
  resave: true}));

server.use(bodyParser.json({limit: '50mb'}));
server.use(bodyParser.urlencoded({limit: '50mb', extended: true}));
server.use(express.static(path.join(__dirname, 'public')));

/**
 * for https
 * @type {{key, cert}}
 */
var options = {
  key: fs.readFileSync(path.join(__dirname, './', 'privkey.pem'))
// You don't need to specify `ca`, it's done by `ssl-root-cas`
//, ca: [ fs.readFileSync(path.join(__dirname, 'server', 'my-private-root-ca.cert.pem'))]
  , cert: fs.readFileSync(path.join(__dirname, './', 'fullchain.pem'))
};

//https.createServer(options, server).listen(443);
//
//server.get('/hello', function(req,res,next){
//  console.log("*** REACHED !!! ***");
//  res.send('hello back!');
//});

/**
 * Authentication
 * @param req - request
 * @param res - response
 * @param next
 */
function myMiddleware (req, res, next) {
  var url = req.url;
  if(S(url).startsWith("/saveUser") || S(url).startsWith("/login") || S(req.method)=='GET'){
    next()

  } else {
    var email = req.body.email;
    var pass = req.body.pass;
    auth.authen(email,pass,function(valid){
      if(valid=='true') next();
      else res.send('not authenticated');
    })
  }
}


//server.use(myMiddleware)

/**************     DEV      ****************/

server.get('/posts/dev/savePost/:is_public/:title/:message/:publisher/:publisherID/:date/:id/*', routeDev.savePost);

server.get('/posts/dev/addComment/:postID/:message/:publisher/:publisherID/:date/:id*', routeDev.addComment);

server.get('/dev/sendNotification/*', routeDev.SendNotification);

server.get('/dev/sendResponse/:child_id/:chat_num/:time/:message/:image/:link/:author_id/:author_name/:author_entity/:image_x/:image_y/*', routeDev.SendResponse);
/*
 example:
 http://10.0.0.1:3001/dev/sendResponse/2/1/1/%D7%94%D7%99%D7%99/%20/%20/1/%D7%A8%D7%96/Manager/%20/%20/
 */
server.get('/chat/dev/saveResponse/:child_id/:num/:time/:message/:author_id/:author_name/:author_entity', routeDev.saveResponse);

server.get('/dev/getConversations/*', routeDev.GetConversations);

server.get('/dev/saveUser/:id/:name/:entity/:phone/:icon/:deviceId/:email*', routeDev.saveUser);
/*
http://10.0.0.1:3001/dev/saveUser/1/raz/Child/052-3390100/1/fV6ADqTD-LI:APA91bF5Y7xhNCgm7ggatOX7BAz49LitPv9jIZOjANvb6QeLcIS5f74gTY01AfYDlSNj1WsbfTjDmQHAL1xHnzW4GDQTpE0h0_jNU2qJg-JpK8d5J-k6a5LZK6eOC0o8jVcYrpg99_wG/raz@google.com
http://10.0.0.1:3001/dev/saveUser/2/dan/Psychologist/052-3390101/1/dtPFvWzq27c:APA91bE-hLsLcVx8wj7qZnpq0MMd-QpyvGkYn_sZd_14sxAyEDYj60UeVyiYkjuyWdU5HNj-Nyx3VeZNEnZ7YjWG_8AAcObpI_SdRDwlgIEd5VVchC7xqfKVgz4Wwto9rf1m-QbG6ieX/dan@google.com
http://10.0.0.1:3001/dev/saveUser/3/eyal/Manager/052-3390101/1/dtPFvWzq27c:APA91bE-hLsLcVx8wj7qZnpq0MMd-QpyvGkYn_sZd_14sxAyEDYj60UeVyiYkjuyWdU5HNj-Nyx3VeZNEnZ7YjWG_8AAcObpI_SdRDwlgIEd5VVchC7xqfKVgz4Wwto9rf1m-QbG6ieX/dan@google.com
*/
server.get('/dev/saveNewQuote/:content/:creator*', routeDev.saveQuote);

server.get('/dev/editQuote/:num/:content/:creator*', routeDev.editQuote);

server.get('/dev/approvedUser/:code/:first_name/:last_name/:entity', routeDev.approvedUser);
//http://10.0.0.1:3001/dev/approvedUser/eoRZqrk_a3o:APA91bEi4LbfKH0pztIIZ6ZCfxvEc5fEciBdbSE-oxbICwUQCdKfvr-Okm1bYSEFoIikxHRTMCEcQkAJ2VHqwcKNZAC27CJXckKITWdJ4OGaRr1SqRJ_EFhkXuL5GyF4INbyi35PZBas/raz/ronen/Child


/**************     LOGIN      ****************/

server.post('/loginCheck', routeGet.checkLogin);

server.post('/login', routeSave.saveLoginRequest);

server.post('/approvedUser', routeSave.approvedUser);

server.post('/saveUser', routeSave.saveUser);

server.get('/login/:email', routeGet.emailExists);

server.get('/login/isApproved/:code', routeGet.isApproved);

server.post('/login/sendPassword/', routeGet.sendPasswordToMail);




/**************     UPDATES      ****************/

server.get('/getUpdates/:num/:amount*', routeGet.getUpdate);

server.get('/editUpdate/:num/:json', routeEdit.editUpdate);        // change to post?






/**************     STATISTICS      ****************/

server.post('/getChildrenRepresentationByName/*', routeGet.getChildrenRepresentationByName);

server.post('/getPsychologistsRepresentationByName/*', routeGet.getPsychologistRepresentationByName);

server.get('/getUserDetails/:id*', routeGet.getUserDetails);




/**************     QUOTES      ****************/

server.get('/getQuote/:num*', routeGet.getQuote);

server.get('/getAllQuotes/*', routeGet.getAllQuotes);

server.get('/deleteQuote/:num*', routeDelete.delQuote);

server.get('/helpRResponse/:num*', routeGet.getHelp);

server.get('/readingQuotes*', routeGet.getReadingQuotes);

server.get('/answeredHelp/:id/:helped*', routeGet.getAnsweredHelp);

server.post('/saveNewQuote/*', routeSave.saveQuote);

server.post('/editQuote/*', routeEdit.editQuote);

server.post('/helpRequest/*', routeSave.saveHelp);







/*************     CALENDAR      **************/

server.get('/getApprovedShiftByPsychoId/:id*', routeGet.getApprovedShiftByPsychoId);

server.get('/getApprovedShift/:id*', routeGet.getShift);

server.get('/shiftCalendar/:max*', routeGet.shiftCalendar);

server.get('/approveShift/:id/:approve/:num*', routeGet.approveShift);

server.get('/timeStatistics/:from/:to*', routeGet.timeStatistics);

server.post('/newShift/*', routeSave.saveShift);

/*
http://10.0.0.2:3001/saveUser/1/raz/Manager/054-3390200/1/XWYZ/raz@google.com
 */
server.get('/setLogin/:id/:time*', routeGet.getLoginTime);

server.get('/destroyApplication*', routeGet.getDestroyApplication);

server.get('/getChatStatus/:id*', routeGet.getChatConversation); // needed?
/**************     USER      ****************/

server.post('/getUser/*', routeGet.GetChildrenByName);                      // chane to post

server.post('/updateChatStatus/*', routeEdit.updateChatConversation);

server.post('/updateUser/:id*', routeSave.saveUser);





/**************     CHAT      ****************/
/*
get children by last messages submitted. - sorted by the last time message of each of the last children message.
 */
server.get('/chat/queueSortByChildLastMessage/*', routeGet.GetChatQueueSortByChildrenLastResponse);
/*
get children by the last psychologist response.
top - the children that got the oldest response from psychologist.(but did sent a message)
bottom - the children that got the recent message from psychologist.
 */
server.get('/chat/queueSortByPsychoLastMessage/*', routeGet.GetChatQueueSortByPsychologistLastResponse);

server.get('/chat/getResponses/:userID/:userIDforConv/:lastMsg/:howMany*', routeGet.GetResponses);

server.post('/chat/saveResponse/*', routeSave.saveResponse);





/**************     POSTS     ****************/

server.get('/posts/getPostsQueue/*', routeGet.getPostsQueue);

server.get('/posts/getAllComments/*', routeGet.getAllComments);

server.get('/posts/getAllPosts/*', routeGet.getAllPosts);

server.get('/posts/getPosts/:userID/:lastTime/:howMany*', routeGet.getPosts);

server.get('/posts/getPost/:postID*', routeGet.getSpecificPost);

server.get('/posts/getComments/:postID/:amount*', routeGet.getComments);

server.get('/posts/deletePost/:id', routeDelete.deletePost);

server.get('/posts/deleteComment/:commentID/:postID', routeDelete.deleteComment);

server.post('/posts/searchPost/*', routeGet.searchPosts);

server.post('/posts/savePost/*', routeSave.savePosts);

server.post('/posts/editPost/:id*', routeEdit.editPost);

server.post('/posts/saveComment/*', routeSave.saveComments);

server.post('/posts/editComment/:id', routeEdit.editComment);



// catch 404 and forward to error handler
server.use(function(req, res, next) {
  res.send("error");
});

 //development error handler
 //catch 404 and forward to error handler
server.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
server.use(function(err, req, res, next) {
  if(err){
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.send('error');
  }
});


//https.createServer(options, server).listen(3001, function() {
//  console.log("API is running and listening on port 3001");
//});
server.listen(3001, function() {
  console.log("API is running and listening on port 3001");
});
