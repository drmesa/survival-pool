# Repository instructions

- Never push code to any Git remote.
- During the `ospx-explore` phase, do not create or modify implementation code. Exploration is read-only; wait until the proposal phase is complete and implementation is explicitly authorized before making code changes.
- Add a Javadoc comment to every newly created method, including private helper methods. Keep the summary concise and describe the method's purpose and behavior; include `@param`, `@return`, and `@throws` tags wherever they clarify the contract. Update Javadoc when changing a method's behavior. Do not add comments to generated accessors or straightforward overrides unless they need clarification.
- Never hardcode or commit secrets, credentials, private keys, or other sensitive configuration. Inject them at runtime through environment variables or an approved secret manager; do not provide real-value defaults. Keep local secret/config files ignored by Git, and use clearly fake placeholders in committed examples. Do not print or expose secret values in logs, test output, or documentation.
