
function loginAccountValidator(data) {
  const errors = [];

  if (!data.email) {
    errors.push({ field: "email", message: "Debes de completar el campo" });
  }

  if (!data.password) {
    errors.push({ field: "password", message: "Debes de completar el campo" });
  }

  return errors;
}

module.exports = { loginAccountValidator };
