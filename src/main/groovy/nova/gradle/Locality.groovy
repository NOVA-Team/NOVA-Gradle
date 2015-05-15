package nova.gradle

import groovy.transform.CompileStatic

@CompileStatic
enum Locality {
	Client, Server;

	public static Locality fromString(String type) {
		switch (type.toLowerCase()) {
			case "client":
				return Client
			case "server":
				return Server
			default:
				throw new IllegalArgumentException("Neither client or server")
		}
	}
}
