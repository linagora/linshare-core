#!/bin/bash
set -e
#set -x

#if [ $# -ne 1 ] ; then
#   echo "ERROR: the first an unique argument is the linshare version number, ex 0.9.3"
#   exit 1
#fi

############################################################
# GLOBAL VARIABLES
############################################################
# Argument : optional : build function name, ex: build_sso
g_main_function=$1
g_set_current_revision=0
#[ -z "${2}" ] && g_set_current_revision=1
g_branch_or_tag="$(git rev-parse --abbrev-ref HEAD)"



g_version=`grep -E "<version>(.*)</version>" pom.xml -o|head -n1|sed -r 's/<version>(.*)<\/version>/\1/g'`
g_logfile=linshare.build.$$.log
g_mvn_opts=""
g_mvn_opts="-Dmaven.test.skip"
g_output_dir="./target"
g_distribution_dir="./distrib"
g_ressources="./src/main/resources"
g_revision=""

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
    local l_extension_sha="sha256sum"
    local l_root_name="linshare-${l_version}"
    local l_ouput_name="linshare-core-${l_version}"

    mv ${g_output_dir}/${l_root_name}.${l_extension} ${g_distribution_dir}/${l_ouput_name}.${l_extension}
    cd ${g_distribution_dir}
    sha256sum ${l_ouput_name}.${l_extension} > ${l_ouput_name}.${l_extension_sha}
    cd -
    echo_linshare "Done."
}

function distribute_war ()
{
    local l_suffix=$1
    local l_version=${g_version}
    local l_extension="war"
    local l_extension_sha="sha256sum"
    local l_root_name="linshare"
    local l_ouput_name="linshare-core-${l_version}"
    if [ ${g_set_current_revision} -eq 1 ] ; then
        if [ ! -z "${g_revision}" ] ; then
            l_ouput_name="${l_ouput_name}-r${g_revision}"
        fi
    fi

    if [ "${l_suffix}" != "" ] ; then
        l_ouput_name="${l_ouput_name}${l_suffix}"
    fi

    mv ${g_output_dir}/${l_root_name}.${l_extension} ${g_distribution_dir}/${l_ouput_name}.${l_extension}
    cd ${g_distribution_dir}
    sha256sum ${l_ouput_name}.${l_extension} > ${l_ouput_name}.${l_extension_sha}
    cd -
    echo_linshare "Done."
}


function init_context ()
{
    g_revision=$(git log -n 1 |grep ^commit| cut -d' ' -f2)
    g_version=`grep -E "<version>(.*)</version>" pom.xml -o|head -n1|sed -r 's/<version>(.*)<\/version>/\1/g'`
    ### Initialisation du workspace.
    echo_linshare "Log file : ${g_logfile}"
    echo_linshare "Current revision : ${g_revision}"
    echo_linshare "Pom version ${g_version}"
    echo_linshare "Building LinShare ${g_version} distribution"
    echo_linshare "Creating distrib dir..."
    #cd $(dirname $0)
    rm -rf ${g_distribution_dir}/
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
function build_doc ()
{
    maven_clean
    build_doc_delegation
    build_doc_userv2
    echo_linshare "Done."
}

function build_doc_userv2 ()
{
    local linshare_output=generated-userv2
    local linshare_source=documentation-webservice-api-user-${g_version}
    local linshare_archive=documentation-webservice-api-user-${g_version}.tar.bz2
    local linshare_sha=documentation-webservice-api-user-${g_version}.sha256sum
    rm -fr ${linshare_output}
    echo_linshare "Building documentation ..."
    maven compile -Pswagger-userv2
    echo_linshare "Archive creation in progress : ${linshare_archive}"
    mv -v ${linshare_output} ${linshare_source}
    tar cjvf ${linshare_archive} ${linshare_source}
    rm -fr ${linshare_source}
    sha256sum ${linshare_archive} > ${linshare_sha}
    mv ${linshare_archive} ${linshare_sha} ${g_distribution_dir}/
}

function build_doc_delegation ()
{
    local linshare_output=generated-delegation
    local linshare_source=documentation-webservice-api-delegation-${g_version}
    local linshare_archive=documentation-webservice-api-delegation-${g_version}.tar.bz2
    local linshare_sha=documentation-webservice-api-delegation-${g_version}.sha256sum
    rm -fr ${linshare_output}
    echo_linshare "Building documentation ..."
    maven compile -Pswagger-delegation
    echo_linshare "Archive creation in progress : ${linshare_archive}"
    mv -v ${linshare_output} ${linshare_source}
    tar cjvf ${linshare_archive} ${linshare_source}
    rm -fr ${linshare_source}
    sha256sum ${linshare_archive} > ${linshare_sha}
    mv ${linshare_archive} ${linshare_sha} ${g_distribution_dir}/
}

function build_source ()
{
    local linshare_archive=linshare-core-${g_version}-src.tar
    local linshare_sha=linshare-core-${g_version}-src.sha256sum

    echo_linshare "Archive creation in progress : ${linshare_archive}"
    git archive --format=tar ${g_branch_or_tag} -o ${g_distribution_dir}/${linshare_archive}
    bzip2 ${g_distribution_dir}/${linshare_archive}
    cd ${g_distribution_dir}
    sha256sum ${linshare_archive}.bz2 > ${linshare_sha}
    cd -
    echo_linshare "Done."
}

function test_linshare ()
{
    # Creation de la version avec SSO
    maven_clean
    echo_linshare "Testing LinShare"
    echo "> mvn test"
    mvn test >> $g_logfile
}

usage()
{
    echo
    echo "Usage : $g_main_function is not a valid function : possible choices are : classic , installer , sso , source, doc, all"
    echo " command target"
    exit 0
}

############################################################
# MAIN
############################################################


if [ -z "$g_main_function" ] ; then
    usage
fi

if [ "${g_main_function}" == "all" ] ; then
    # Initialisation du workspace.
    init_context

    # Testing LinShare
    test_linshare

    # Creation de la version avec installeur.
    build_installer

    # Creation de la version sans SSO
    build_classic

    # Creation de la version avec CAS
    # Deprecated
    #build_cas

    # Creation de la version avec SSO
    build_sso

    # Creation de la documentation
    build_doc

    # Creation de l'archive des sources
    build_source
else
    if [ `declare -F "build_${g_main_function}"|wc -l` -eq 1 ] ; then
        # Initialisation du workspace.
        init_context
        "build_${g_main_function}"
    else
        usage
    fi
fi

# Clean du workspace.
end_context
