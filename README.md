# Multiplayer
Java Game utilizing network sockets and JSwing for a multiplayer server setting game

Tank Game where players are tanks that shoot at each other. Utilization of server-side calculations and positions for synchronized
visuals across all screens, only differing by the colors of tanks. 

JAR deployments, need to edit client code so that it looks for the right machine, info found in cmd through ipconfig
server runs first then the clients will join the server game. Server can reset the game by pressing r in the server window.

Clients control tanks with W - forward, A - turn counter clockwise, S - backwards, D - turn clockwise, and Space to shoot in the
direction currently facing. Limit of 10 bullets shot at a time, lifespan of 10 seconds per bullet. If hit tank dies and is out for
the round, last tank standing wins round with a 4 second period where they can still be hit and cause a draw before round ends. 
