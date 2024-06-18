## ERD

- Order : LinkNode = 1 : N
- LinkNode : Post(Publication) = 1 : 1
- LinkNode : Comment(Publication) = 1 : 1
- Post : Webpage = N : 1
- Comment : CommentableWebpage = N : 1
- Webpage : Repository = 1 : 1
- Repository : GitAccount = N : 1