package com.maxtrain.bootcamp.requestline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.maxtrain.bootcamp.request.RequestRepository;

@CrossOrigin
@RestController
@RequestMapping("api/requestlines")

public class RequestlineController {
	@Autowired
	private RequestRepository reqRepo;
	@Autowired
	private RequestlineRepository rqlnRepo;
	
		
	@SuppressWarnings("rawtypes")
	private ResponseEntity recalcRequestTotal(int requestId) {
		var reqOpt = reqRepo.findById(requestId);
		if(reqOpt.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		var request = reqOpt.get();
		var requestTotal = 0;
		for(var requestline : request.getRequestlines()) {
			requestTotal += requestline.getProduct().getPrice() * requestline.getQuantity();
		}
		request.setTotal(requestTotal);
		reqRepo.save(request);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<Iterable<Requestline>> getRequestlines() {
		var requestlines = rqlnRepo.findAll();
		return new ResponseEntity<Iterable<Requestline>>(requestlines, HttpStatus.OK);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Requestline> getRequestline(@PathVariable int id) {
		var requestline = rqlnRepo.findById(id);
		if(requestline.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Requestline>(requestline.get(), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Requestline> postRequestline(@RequestBody Requestline requestline) throws Exception {
		if(requestline == null || requestline.getId() != 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var rqln = rqlnRepo.save(requestline);
		var responseEnt = this.recalcRequestTotal(rqln.getRequest().getId());
		if(responseEnt.getStatusCode() != HttpStatus.OK) {
			throw new Exception("Recalculate request total failed!");
		}
		return new ResponseEntity<Requestline>(rqln, HttpStatus.CREATED);
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}")
	public ResponseEntity putRequestline(@PathVariable int id, @RequestBody Requestline requestline) throws Exception {
		if(requestline == null || requestline.getId() != id) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var rqlnNF = rqlnRepo.findById(id);
		if(rqlnNF.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		var rqln = rqlnNF.get();
		rqlnRepo.save(requestline);
		var responseEnt = this.recalcRequestTotal(rqln.getRequest().getId());
		if(responseEnt.getStatusCode() != HttpStatus.OK) {
			throw new Exception("Recalculate request total failed!");
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	
	@SuppressWarnings("rawtypes")
	@DeleteMapping("{id}")
	public ResponseEntity putRequestline(@PathVariable int id) throws Exception {
		var requestlineNF = rqlnRepo.findById(id);
		if(requestlineNF.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		var requestline = requestlineNF.get();
		rqlnRepo.delete(requestline);
		var responseEnt = this.recalcRequestTotal(requestline.getId());
		if(responseEnt.getStatusCode() != HttpStatus.OK) {
			throw new Exception("Recalculate request total failed!");
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
