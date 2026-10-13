{
  description = "Developer shell for openapi-spec-for-spring";

  inputs = {
    nixpkgs.url = "nixpkgs/nixos-26.05";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in
      {
        devShells.default = pkgs.mkShell {
          # Gradle comes from ./gradlew. The build's Java toolchain is JDK 21.
          packages = [ pkgs.jdk21_headless ];
        };
      }
    );
}
