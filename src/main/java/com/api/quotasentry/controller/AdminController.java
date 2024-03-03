package com.api.quotasentry.controller;

import com.api.quotasentry.model.DbType;
import com.api.quotasentry.model.User;
import com.api.quotasentry.service.AdminDbService;
import com.api.quotasentry.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("admin")
public class AdminController {

    private final AdminDbService adminDbService;

    @Autowired
    public AdminController(AdminDbService adminDbService) {
        this.adminDbService = adminDbService;
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/admin/delete"
     *
     * @return
     */
    @GetMapping("delete") // todo change to @PostMapping when all works
    public ResponseEntity<String> deleteDataFromDbs() {
        adminDbService.deleteDataFromDbs();
        return ResponseEntity.ok().body("Data deleted from both DBs");
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/admin/seed"
     *
     * @return
     */
    @GetMapping("seed") // todo change to @PostMapping when all works
    public ResponseEntity<String> seedDataToDbs() {
        adminDbService.seedDataToDbs();
        return ResponseEntity.ok().body("Data seeded to both DBs");
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/admin/getdata"
     *
     * @return
     */
    @GetMapping("getdata")
    public ResponseEntity<String> getDataFromDbs() {
        List<User> users = adminDbService.getDataFromDbs();
        return ResponseEntity.ok().body(JsonUtils.toJson(users));
    }

    /**
     * url examples.
     *
     * "http://localhost:8080/admin/getdata/Elastic"
     * "http://localhost:8080/admin/getdata/Mysql"
     *
     * @return
     */
    @GetMapping("getdata/{dbType}")
    public ResponseEntity<String> getDataFromDb(@PathVariable DbType dbType) {
        List<User> users = adminDbService.getDataFromDb(dbType);
        return ResponseEntity.ok().body(JsonUtils.toJson(users));
    }
}
