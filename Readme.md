
### Test task

[Test_task](task.md)

run application
```shell
java -jar -Dsecret=qazWSXedc -Dmock=http://localhost:8888/ internal-0.0.1-SNAPSHOT.jar 
```

run tests

```shell
mvn clean test allure:serve
```
