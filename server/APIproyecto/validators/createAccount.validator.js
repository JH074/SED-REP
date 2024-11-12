const passRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,32})/;

function createAccountValidator(data) {
  const errors = [];

  if (!data.username || data.username.length < 2 || data.username.length > 15) {
    errors.push({ field: "username", message: "El campo 'username' debe tener entre 2 y 15 caracteres." });
  }

  if (!data.email) {
    errors.push({ field: "email", message: "Debes de completar el campo" });
  } else if (!/\S+@\S+\.\S+/.test(data.email) || data.email.length < 5 || data.email.length > 35) {
    errors.push({ field: "email", message: "Correo electrónico incorrecto o longitud inválida." });
  }

  if (!data.password) {
    errors.push({ field: "password", message: "Debes de completar el campo" });
  } else if (!passRegex.test(data.password)) {
    errors.push({
      field: "password",
      message: "Debe tener al menos 8 caracteres, incluyendo números, mayúsculas, minúsculas y caracteres especiales."
    });
  }

  if (!data.year_nac) {
    errors.push({ field: "year_nac", message: "Debes de completar el campo" });
  } else {
    const birthDate = new Date(data.year_nac);
    const twelveYearsAgo = new Date();
    twelveYearsAgo.setFullYear(twelveYearsAgo.getFullYear() - 12);

    if (birthDate > twelveYearsAgo) {
      errors.push({ field: "year_nac", message: "Debes tener al menos 12 años de edad." });
    }
  }

  if (!data.genere || data.genere.length < 2 || data.genere.length > 15) {
    errors.push({ field: "genere", message: "El campo 'genere' debe tener entre 2 y 15 caracteres." });
  }

  if (!data.movie_genere || data.movie_genere.length < 3) {
    errors.push({ field: "movie_genere", message: "Debes seleccionar al menos 3 géneros de películas." });
  }

  if (!data.avatar) {
    errors.push({ field: "avatar", message: "Debes de elegir un avatar" });
  }

  return errors;
}

module.exports = { createAccountValidator };
