<#import "main.ftl" as layout />

<@layout.mainLayout title="mock-oauth2-server" description="Just a mock login">
<div class="container">
    <script>
        const users = ${user_config};

        function createRadioButtons() {
            const el = document.getElementById("userForm");
            users.forEach(user => {
                const input = document.createElement("input");
                input.type = "radio";
                input.name = "userSelection";
                input.value = user.name;
                input.style.marginRight = "12px";

                const label = document.createElement("label");
                label.appendChild(input);
                label.append(user.name);

                el.appendChild(label);
            });
        }

        function handleSubmit(event) {
            const checkedRadioButton = document.querySelector('input[name="userSelection"]:checked');
            if (!checkedRadioButton) {
                event.preventDefault();
                return;
            }
            const selectedUser = users.find((u) => u.name === checkedRadioButton.value);
            if (!selectedUser) {
                event.preventDefault();
                return;
            }
            document.getElementById("username").value = selectedUser.name;
            document.getElementById("claims").value = selectedUser.claims;
        }

        function showInputs() {
            document.getElementById("username").type = "text";
            document.getElementById("claims").type = "text";
        }

        function init() {
            if (!users || !users.length) {
                showInputs();
            } else {
                createRadioButtons();
                document.getElementById("submitButton").addEventListener("click", (event) => handleSubmit(event));
            }
        }

        window.addEventListener("load", (event) => init());
    </script>
    <section class="header">
        <h2 class="title">OAuth2 Demo Sign-in</h2>
    </section>
    <div class="docs-section" id="sign-in">
        <div class="row">
            <div class="three columns">&nbsp;</div>
            <div class="six columns">
                <form method="post" id="userForm"></form>

                <form method="post" id="submitForm">
                    <label>
                        <input class="u-full-width" required type="hidden" name="username"
                               placeholder="Enter any user/subject" id="username"
                               autofocus="on">
                    </label>
                    <label>
                        <input class="u-full-width" type="hidden" name="claims"
                               placeholder="Optional claims JSON value" id="claims"
                               autofocus="on">
                    </label>
                    <input class="button-primary" type="submit" id="submitButton" value="Sign-in">
                </form>
            </div>
        </div>
    </div>
</div>
</@layout.mainLayout>
