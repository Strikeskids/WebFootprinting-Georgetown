#!/bin/sh
cd bin

echo "Agent-Class: BindIPAgent\nManifest-Version: 1.0" > tmp
jar -cvmf tmp ../agent.jar BindIPAgent*.class NetworkAddresses*.class
rm tmp
jar -cvf ../boot.jar NetworkAddresses*.class
