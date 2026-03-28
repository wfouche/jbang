set  SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 21.0.10-tem

export JBANG=~/IdeaProjects/jbang/build/install/jbang/bin/jbang
# $JBANG run cli --version
# $JBANG run h
# $JBANG run h:1.0.1

echo ""
echo "Hello world tests"
echo ""
$JBANG run hw
$JBANG run hw2

echo ""
echo "Camel tests"
echo ""
$JBANG run camel version
$JBANG run camel:4.17.0 version

