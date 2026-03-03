ConnectionState
||
|| connect to serv
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
|| roles are ready      || end of game
\/                      ||
GamePlayState ------------