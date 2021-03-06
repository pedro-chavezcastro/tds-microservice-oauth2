package co.com.viveres.susy.microserviceoauth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.com.viveres.susy.microserviceoauth.clients.IUserFeignClient;
import co.com.viveres.susy.microserviceoauth.dto.RoleDto;
import co.com.viveres.susy.microserviceoauth.dto.UserDto;
import feign.FeignException;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

	@Autowired
	private IUserFeignClient client;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		try {
			UserDto user = this.findByUsername(username);
			
			List<GrantedAuthority> authorities = user.getRoles()
					.stream()
					.map(RoleDto::getName)
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
			
			return new User(user.getUsername(), user.getPassword(), user.getEnabled(),
					true, true, true, authorities);
		} catch (FeignException e) {
			throw new UsernameNotFoundException(
					String.format("Error en login, no existe el usuario %s", username));
		}
	}

	@Override
	public UserDto findByUsername(String username) {
		ResponseEntity<UserDto> response = this.client.findByUsername(username);
		return response.getBody();
	}

	@Override
	public UserDto findById(Long id) {
		ResponseEntity<UserDto> response = this.client.findById(id);
		return response.getBody();
	}

	@Override
	public void update(Long id, UserDto user) {
		this.client.update(id, user);
	}

}
