#!/bin/bash
set -e

#if [ $# -ne 1 ] ; then
#	echo "ERROR: the first an unique argument is the linshare version number, ex 0.9.3"
#	exit 1 
#fi

############################################################
# GLOBAL VARIABLES
############################################################
# Argument : optional : build function name, ex: build_sso
g_main_function=$1


g_version=`grep -E "<version>(.*)</version>" pom.xml -o|head -n1|sed -r 's/<version>(.*)<\/version>/\1/g'`
g_logfile=linshare.build.$$.log
#g_mvn_opts="-Dmaven.test.skip"
g_mvn_opts=""
g_output_dir="./target"
g_distribution_dir="./distrib"
g_ressources="./src/main/resources"


############################################################
# FUNCTIONS
############################################################
function echo_linshare () 
{
	local l_args=$@
	echo "[INFO] ${l_args}"
	echo "[BUILD-LINSHARE-SCRIPT][INFO] ${l_args}" >> $g_logfile
}

function maven () 
{
	echo "> mvn $@ ${g_mvn_opts}"
	mvn $@ ${g_mvn_opts} >> $g_logfile

}

function maven_clean () 
{
	echo_linshare "Cleaning maven compiled files..."
	echo "> mvn clean"
	mvn clean >> $g_logfile
}

function distribute_jar ()
{

	local l_version=${g_version} 
	local l_extension="jar"
	local l_root_name="linshare-${l_version}"
	local l_ouput_name="linshare-${l_version}"

	mv ${g_output_dir}/${l_root_name}.${l_extension} ${g_distribution_dir}/${l_ouput_name}.${l_extension}
	echo_linshare "Done."
}

function distribute_war ()
{
	local l_suffix=$1

	local l_version=${g_version} 
	local l_extension="war"
	local l_root_name="linshare"
	local l_ouput_name="linshare-${l_version}"

	if [ "${l_suffix}" != "" ] ; then
		l_ouput_name="${l_ouput_name}${l_suffix}"
	fi

	mv ${g_output_dir}/${l_root_name}.${l_extension} ${g_distribution_dir}/${l_ouput_name}.${l_extension}
	echo_linshare "Done."
}


function init_context () 
{
	### Initialisation du workspace.
	echo_linshare "Log file : ${g_logfile}"
	echo_linshare "Building LinShare ${g_version} distribution"
	echo_linshare "Creating distrib dir..."
	rm -f ${g_distribution_dir}/*
	mkdir -p ${g_distribution_dir}
}

function end_context () 
{
	# Clean du workspace.
	maven_clean
	echo_linshare "END."
}
	
function build_installer ()
{
	# Creation de la version avec installeur.
	maven_clean
	echo_linshare "Building jar installer..."
	maven package -Dtarget=distribution
	distribute_jar
}

function build_classic ()
{
	# Creation de la version sans SSO
	maven_clean
	echo_linshare "Building war without SSO..."
	maven package
	distribute_war "-without-SSO"
}

function build_cas ()
{
	# Creation de la version avec CAS
	maven_clean
	echo_linshare "Building war for CAS..."
	mv ${g_ressources}/{,DISABLED}springContext-security.xml
	mv ${g_ressources}/{DISABLED,}springContext-securityCAS.xml
	maven package
	distribute_war "-CAS"
	mv ${g_ressources}/{DISABLED,}springContext-security.xml
	mv ${g_ressources}/{,DISABLED}springContext-securityCAS.xml
}

function build_sso ()
{
	# Creation de la version avec SSO
	maven_clean
	echo_linshare "Building war for HTTP-Header based SSO..."
	mv ${g_ressources}/{,DISABLED}springContext-security.xml
	mv ${g_ressources}/{DISABLED,}springContext-securityLLNG.xml
	maven package
	distribute_war "-SSO"
	mv ${g_ressources}/{DISABLED,}springContext-security.xml
	mv ${g_ressources}/{,DISABLED}springContext-securityLLNG.xml
}

############################################################
# MAIN
############################################################

# Initialisation du workspace.
init_context

if [ -z $g_main_function ] ; then 
	# Creation de la version avec installeur.
	build_installer

	# Creation de la version sans SSO
	build_classic

	# Creation de la version avec CAS
	build_cas

	# Creation de la version avec SSO
	build_sso
else
	if [ `declare -F $g_main_function|wc -l` -eq 1 ] ; then 
		$g_main_function
	else
		echo "ERROR:$g_main_function is not a valid function : possible choices are : build_classic , build_installer , build_cas , build_sso "
		exit 1
	fi
fi

# Clean du workspace.
end_context
