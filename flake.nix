{
    inputs = {
        nixpkgs.url = "github:nixos/nixpkgs/nixpkgs-unstable";
        flake-utils.url = "github:numtide/flake-utils";
    };

    outputs = { self, nixpkgs, flake-utils }:
        flake-utils.lib.eachDefaultSystem(system:
            let
                pkgs = import nixpkgs {
                    inherit system;
                };
            in
            with pkgs;
            {
                devShells.default = mkShell rec {
                    nativeBuildInputs = [
                    	mesa-demos
                    	pciutils
                    	xorg.xrandr
                    ];

                    buildInputs = [
                    	(lib.getLib stdenv.cc.cc)
                        ## native versions
						glfw3-minecraft
						openal

						## openal
						alsa-lib
						libjack2
						libpulseaudio
						pipewire

						## glfw
						libGL
						xorg.libX11
						xorg.libXcursor
						xorg.libXext
						xorg.libXrandr
						xorg.libXxf86vm

						udev # oshi

						vulkan-loader # VulkanMod's lwjgl
                    ];

                    LD_LIBRARY_PATH = lib.makeLibraryPath buildInputs + "${addDriverRunpath.driverLink}/lib";
                };
            }
        );
}