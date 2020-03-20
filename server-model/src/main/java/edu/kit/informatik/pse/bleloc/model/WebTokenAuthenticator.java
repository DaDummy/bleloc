package edu.kit.informatik.pse.bleloc.model;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class
WebTokenAuthenticator implements Authenticator {

	private static final SignatureAlgorithm SigningAlgorithm = SignatureAlgorithm.HS384;
	private static String AccountIdClaimName = "id";

	private final AuthenticatorPurpose purpose;
	private final Long invalidAccountId;

	private Key SigningKey;

	WebTokenAuthenticator(AuthenticatorPurpose purpose, Long invalidAccountId) {
		this.purpose = purpose;
		this.invalidAccountId = invalidAccountId;

		SigningKey = Keys.secretKeyFor(SigningAlgorithm);
	}

	@Override
	public java.lang.String createCookie(Long userId) {
		return Jwts.builder().signWith(SigningKey, SigningAlgorithm).claim(AccountIdClaimName, userId)
		           .setAudience(purpose.toString()).setIssuedAt(new Date()).compact();
	}

	@Override
	public Long verifyCookie(String cookie) {
		try {
			// Parse the web token
			Claims parsedJsonWebTokenBody = Jwts.parser().setSigningKey(SigningKey).parseClaimsJws(cookie).getBody();

			boolean isValidToken = true;

			// Validation: Check if the token was issued in the past
			isValidToken &= parsedJsonWebTokenBody.getIssuedAt().before(new Date());

			// Validation: Check if the token was issued for this Authenticator's purpose
			isValidToken &= parsedJsonWebTokenBody.getAudience().equals(purpose.toString());

			// If the token passed validation get the account id and return it
			if (isValidToken) {
				return parsedJsonWebTokenBody.get(AccountIdClaimName, Long.class);
			}
		} catch (Exception e) {
			// No explicit error handling necessary: Just fall through to the invalid account id return
		}

		return invalidAccountId;
	}
}
