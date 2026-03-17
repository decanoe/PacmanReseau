ConnectionState
||
|| connect to serv
\/
LoginState
||
|| login
\/
LobyState ----------------
||                      /\
|| select room          || leave room
\/                      ||
GameWaitState ------------
||                      
|| new game is ready
\/
GameRoleState ------------
||                      /\
|| roles are ready      ||
\/                      ||
GamePlayState --------- ||
||                      ||
|| end of game          || wait 
\/                      ||
GameResultState ----------