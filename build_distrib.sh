#!/bin/bash

echo "[BUILD-LINSHARE-SCRIPT] Building LinShare $1 distribution"

echo "[BUILD-LINSHARE-SCRIPT] Creating distrib dir..."
rm -ri distrib
mkdir distrib

echo "[BUILD-LINSHARE-SCRIPT] Cleaning maven compiled files..."
mvn clean

echo "[BUILD-LINSHARE-SCRIPT] Building jar installer..."
mvn package -Dtarget=distribution

mv target/linshare-$1.jar distrib/.

echo "[BUILD-LINSHARE-SCRIPT] Building war without SSO..."
mvn clean package -Dmaven.test.skip=true

mv target/linshare.war distrib/linshare-$1-without-SSO.war

echo "[BUILD-LINSHARE-SCRIPT] Building war for CAS..."
mv src/main/resources/springContext-security.xml src/main/resources/DISABLEDspringContext-security.xml

mv src/main/resources/DISABLEDspringContext-securityCAS.xml src/main/resources/springContext-securityCAS.xml

mvn clean package -Dmaven.test.skip=true

mv target/linshare.war distrib/linshare-$1-CAS.war

mv src/main/resources/springContext-securityCAS.xml src/main/resources/DISABLEDspringContext-securityCAS.xml

echo "[BUILD-LINSHARE-SCRIPT] Building war for HTTP-Header based SSO..."
mv src/main/resources/DISABLEDspringContext-securityLLNG.xml src/main/resources/springContext-securityLLNG.xml

mvn clean package -Dmaven.test.skip=true

mv target/linshare.war distrib/linshare-$1-SSO.war

mv src/main/resources/springContext-securityLLNG.xml src/main/resources/DISABLEDspringContext-securityLLNG.xml

mv src/main/resources/DISABLEDspringContext-security.xml src/main/resources/springContext-security.xml

echo "[BUILD-LINSHARE-SCRIPT] Cleaning maven compiled files..."
mvn clean

echo "[BUILD-LINSHARE-SCRIPT] done."
