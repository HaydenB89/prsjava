package com.maxtrain.bootcamp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/users")
public class UserController {
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("login/{username}/{password}")
	public ResponseEntity<User> login(@PathVariable String username, @PathVariable String password){
		var user = userRepo.findByUsernameAndPassword(username, password);
		if(user.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(user.get(), HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<Iterable<User>> getUsers(){
		var users = userRepo.findAll();
		return new ResponseEntity<Iterable<User>>(users,HttpStatus.OK);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<User> getUser(@PathVariable int id) {
		var user = userRepo.findById(id);
		if(user.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(user.get(), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<User> postUser(@RequestBody User user) {
		if(user == null || user.getId() != 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var newuser = userRepo.save(user);
		return new ResponseEntity<User>(newuser, HttpStatus.CREATED);
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}")
	public ResponseEntity putUser(@PathVariable int id, @RequestBody User user) {
		if(user == null || user.getId() == 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var userEdit = userRepo.findById(user.getId());
		if(userEdit.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		userRepo.save(user);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping("{id}")
	public ResponseEntity putVendor(@PathVariable int id) {
		var user = userRepo.findById(id);
		if(user.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		userRepo.delete(user.get());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
