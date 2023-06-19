db.createCollection( "messages", { capped: true, size: 100000 } )
db.createCollection( "chats", { capped: true, size: 100000 } )