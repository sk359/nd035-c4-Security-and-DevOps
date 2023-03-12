package com.example.demo.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class MockAuthentication {
	
	boolean _authenticated;
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<>();
	}
	
	public Object getCredentials() {
		return null;
	}
	
	public Object getDetails() {
		return null;
	}
	
	public Object getPrincipal() {
		return null;
	}
	
	public boolean isAuthenticated() {
		return _authenticated;
	}
	
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		_authenticated = isAuthenticated;
	}

}
