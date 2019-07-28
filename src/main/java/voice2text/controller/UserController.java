package voice2text.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import voice2text.entity.User;

/**
 * @author ganchaoyang
 * @date 2019/3/1013:55
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口", description = "提供用户相关的Rest API")
public class UserController {

    @ApiOperation("新增用户接口")
    @PostMapping("/add")
    public boolean addUser(@RequestBody User user) {
        return false;
    }

    @ApiOperation("通过id查找用户接口")
    @GetMapping("/find/{id}")
    public User findById(@PathVariable("id") int id) {
        return new User();
    }

    @ApiOperation("更新用户信息接口")
    @PutMapping("/update")
    public boolean update(@RequestBody User user) {
        return true;
    }

    @ApiOperation("删除用户接口")
    @DeleteMapping("/delete/{id}")
    @ApiIgnore
    public boolean delete(@PathVariable("id") int id) {
        return true;
    }
}
