set  SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 21.0.10-tem

#../build/install/jbang/bin/jbang run cli --version
#../build/install/jbang/bin/jbang run h
#../build/install/jbang/bin/jbang run h:1.0.1

#../build/install/jbang/bin/jbang run hw
../build/install/jbang/bin/jbang run hw2

#../build/install/jbang/bin/jbang run camel version
../build/install/jbang/bin/jbang run camel:4.17.0 version

