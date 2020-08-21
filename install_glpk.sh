### This script is based on http://glpk-java.sourceforge.net/gettingStarted.html.
### It installs glpk into your home directory - so it only needs to be done once per person, not per server.
### If it should be installed on the server for all to use, it can be easily modified.

# Install directory
BASE_DIRECTORY=/home/$USER
INSTALL_DIRECTORY=$BASE_DIRECTORY/glpk

# Set JAVA_HOME
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac))))

# Download source code
mkdir -p $BASE_DIRECTORY/src
cd $BASE_DIRECTORY/src
rm -rf glpk-glpk_4_65*
wget http://ftp.gnu.org/gnu/glpk/glpk-4.65.tar.gz
tar -xzf glpk-4.65.tar.gz
rm -rf glpk-java-1.12.0*
wget http://download.sourceforge.net/project/glpk-java/\
glpk-java/glpk-java-1.12.0/libglpk-java-1.12.0.tar.gz
tar -xzf libglpk-java-1.12.0.tar.gz

# Build and install GLPK
cd $BASE_DIRECTORY/src/glpk-4.65
./configure --prefix=$INSTALL_DIRECTORY
make -j6
make check
make install

# Build and install GLPK for Java
cd $BASE_DIRECTORY/src/libglpk-java-1.12.0
export CPPFLAGS=-I$INSTALL_DIRECTORY/include
export SWIGFLAGS=-I$INSTALL_DIRECTORY/include
export LD_LIBRARY_PATH=$INSTALL_DIRECTORY/lib
# --enable-libpath may or may not be necessary depending on the server/computer
./configure --prefix=$INSTALL_DIRECTORY --enable-libpath
make
make check
make install
unset CPPFLAGS
unset SWIGFLAGS
