###################
# PARAMETRIZACOES #
###################

.DEFAULT_GOAL := help

SHELL := /bin/bash
CURRENT_DIR:=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
ROOT_DIR := $(HOME)
PROJECT_NAME := "simple-http-server"

CONTEXT = "Clojure Tools"
NO_COLOR=\x1b[0m
GREEN_COLOR=\x1b[32;01m
RED_COLOR=\x1b[31;01m
ORANGE_COLOR=\x1b[33;01m

OK_STRING=$(GREEN_COLOR)[OK]$(NO_COLOR)
ERROR_STRING=$(RED_COLOR)[ERRORS]$(NO_COLOR)
WARN_STRING=$(ORANGE_COLOR)[WARNINGS]$(NO_COLOR)


#############
# FUNCTIONS #
#############

define critical_question
    echo -e "\t$(RED_COLOR)$(1)$(NO_COLOR) "
		while true; do \
	    read -p '          Informe: (y/n)' yn ; \
	    case $$yn in  \
	        y|Y ) echo -e "              $(GREEN_COLOR)Continuando...$(NO_COLOR)"; break ;; \
	        n|N ) echo -e "              Ok... $(RED_COLOR)saindo, cancelando, desistindo....$(NO_COLOR)"; sleep 2; exit 255 ;; \
	        * ) echo "              Por favor, escolha y ou n." ;; \
	     esac ; \
	  done
endef
define msg_critical
    echo -e "$(RED_COLOR)-->[$(1) ]$(NO_COLOR)\n"
endef

define msg_warn
    echo -e "$(ORANGE_COLOR)-->[$(1) ]$(NO_COLOR)\n"
endef

define msg_ok
    echo -e "$(GREEN_COLOR)-->[$(1) ]$(NO_COLOR)\n"
endef

define menu
    echo -e "$(GREEN_COLOR)[$(1) ]$(NO_COLOR)"
endef

########################
# BINARIOS E PROGRAMAS #
########################
FIND_MAKE=find $(ROOT_DIR) -name Makefile


###########################
# INTERNO PARA O MAKE.... #
###########################
.PHONY: clear_screen
clear_screen:
	@clear

.PHONY: quit
sair:
	@clear

###############
# FERRAMENTAS #
###############
.PHONY: docker_clear_missing_images
docker_clear_missing_images: ## Limpa as imagens docker que falharam a construção
		@docker rmi $$(docker images -q -f dangling=true) || echo

.PHONY: docker_clear_containers
docker_clear_containers: ## Exclui container com status exited
		@docker rm -vf $$(docker ps -qa -f "status=exited") || echo


.PHONY: docker_clear_all
docker_clear_all: ## Limpa o ambiente local do docker
	  make docker_limpa_imagens_perdidas
	  make docker_limpa_containers
		docker system prune -f

.PHONY: build_app
build_app: version ## Crea imagem docker da aplicação
	@$(call msg_ok, "Building app $$(PROJECT_NAME) in version $$(cat version)")
	@docker build --no-cache -t simple-http-server:$$(cat version) -f Dockerfile .

.PHONY: test_app
test_app: ## Executa os testes da aplicação
	@clj -A:test -m kaocha.runner

.PHONY: test_coverage
test_coverage: ## Executa teste de cobertura de código
	@clj -A:test -m kaocha.runner --plugin cloverage

.PHONY: lint
lint: ## Verifica possíveis problemas no código
	@clojure -A:lint

#######################
## tools - MENU MAKE ##
#######################
.PHONY: help
help: clear_screen
	@$(call menu, "============== $(CONTEXT) ==============")
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST)  | awk 'BEGIN {FS = ":.*?## "}; {printf "\t\033[36m%-30s\033[0m %s\n", $$1, $$2}'
