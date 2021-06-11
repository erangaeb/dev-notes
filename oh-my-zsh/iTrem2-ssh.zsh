function tabc() {
  NAME=$1; if [ -z "$NAME" ]; then NAME="Default"; fi 
  # if you have trouble with this, change
  # "Default" to the name of your default theme
  echo -e "\033]50;SetProfile=$NAME\a"
}

function tab-reset() {
    NAME="Default"
    echo -e "\033]50;SetProfile=$NAME\a"
}

function colorssh() {
    if [[ -n "$ITERM_SESSION_ID" ]]; then
        trap "tab-reset" INT EXIT
        if [[ "$*" =~ "production*" ]]; then
            tabc Production
        elif [[ "$*" =~ "staging*" ]]; then
            tabc Staging 
        else
            tabc Other
        fi
    fi
    ssh $*
    trap - INT
}
compdef _ssh tabc=ssh

alias ssh="colorssh"
