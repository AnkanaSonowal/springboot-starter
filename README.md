# springboot-starter
Basic springboot application for storing object details in PostGre sql enabled with redis caching
If the same endpoints are triggered more than 2 times, only then it gets added to the redis cache, else store in Postgres db
Basically if frequently visited links only then add to cache.


PostGre SQl is run using Docker image.
Using Redis server kept tracked of redis cache getting added.
separating the logic into various packages: model, DAO, Datasource, config, service,API
