package com.ic.cinefile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ic.cinefile.R
import com.ic.cinefile.viewModel.userCreateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Configuraciones(viewModel: userCreateViewModel,navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        text = "Configuraciones",
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(6.dp)
                            .clickable { navController.popBackStack() },
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Black,
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.logout()

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_output_24),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "Cerrar sesion",
                            color = Color.White,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Terminos()
        }
    }
}

@Composable
fun Terminos() {

    var expandedTerminos by remember { mutableStateOf(false) }
    var expandedPrivacidad by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Términos de uso
        Text(
            text = "Términos de uso",
            color = Color.White,
            fontSize = 18.sp,

            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable {
                    expandedTerminos = !expandedTerminos
                }
        )

        // Texto expandido de Términos de uso
        if (expandedTerminos) {
            Text(
                text = "Términos de Uso \n" +
                        "Fecha de Vigencia: 1/06/2024\n" +
                        "\n" +
                        "Bienvenido a Cinefile. Estos términos de uso (\"Términos\") rigen su acceso y uso de nuestra aplicación móvil donde los usuarios pueden calificar, comentar, responder comentarios, buscar y guardar películas. Al utilizar nuestra aplicación, usted acepta estos Términos en su totalidad. Si no está de acuerdo con alguno de estos Términos, no debe usar la aplicación.\n" +
                        "\n" +
                        "1. Descripción del Servicio\n" +
                        "CineFile es una plataforma que permite a los usuarios:\n" +
                        "\n" +
                        "Calificar películas.\n" +
                        "Comentar sobre películas.\n" +
                        "Responder a comentarios de otros usuarios.\n" +
                        "Buscar películas.\n" +
                        "Guardar películas en una lista personal.\n" +
                        "2. Aceptación de los Términos\n" +
                        "Al acceder o utilizar nuestra aplicación, usted acepta estar sujeto a estos Términos y a nuestra Política de Privacidad.\n" +
                        "\n" +
                        "3. Uso Permitido\n" +
                        "Usted se compromete a:\n" +
                        "\n" +
                        "Usar la aplicación solo para fines personales y no comerciales.\n" +
                        "No utilizar la aplicación para ningún propósito ilegal o no autorizado.\n" +
                        "No suplantar a ninguna persona o entidad, ni falsificar su identidad.\n" +
                        "No publicar contenido que sea ofensivo, difamatorio, ilegal, o que viole los derechos de otros.\n" +
                        "4. Registro y Seguridad de la Cuenta\n" +
                        "Para usar ciertas funciones de la aplicación, es posible que deba registrarse y crear una cuenta. Usted es responsable de mantener la confidencialidad de su información de inicio de sesión y de todas las actividades que ocurran bajo su cuenta.\n" +
                        "\n" +
                        "5. Propiedad Intelectual\n" +
                        "Todos los derechos de autor, marcas comerciales y otros derechos de propiedad intelectual en la aplicación y su contenido (excluyendo el contenido generado por los usuarios) son propiedad de [Nombre de la Empresa] o sus licenciantes.\n" +
                        "\n" +
                        "6. Contenido Generado por los Usuarios\n" +
                        "Al publicar comentarios, calificaciones u otro contenido en la aplicación, usted otorga a [Nombre de la Empresa] una licencia mundial, no exclusiva, libre de regalías, para usar, copiar, modificar, y distribuir dicho contenido.\n" +
                        "\n" +
                        "7. Moderación de Contenido\n" +
                        "[Nombre de la Empresa] se reserva el derecho de moderar, eliminar o editar cualquier contenido que viole estos Términos o que consideremos inapropiado.\n" +
                        "\n" +
                        "8. Privacidad y Datos Personales\n" +
                        "Para más detalles sobre cómo recopilamos, utilizamos y protegemos su información personal.\n" +
                        "\n" +
                        "9. Limitación de Responsabilidad\n" +
                        "[Nombre de la Empresa] no será responsable de ningún daño directo, indirecto, incidental, especial o consecuente que resulte del uso o la incapacidad de usar la aplicación.\n" +
                        "\n" +
                        "10. Modificaciones de los Términos\n" +
                        "Nos reservamos el derecho de modificar estos Términos en cualquier momento. Las modificaciones serán efectivas al publicarlas en la aplicación. Su uso continuado de la aplicación después de cualquier cambio constituye su aceptación de los nuevos Términos.\n" +
                        "\n" +
                        "11. Terminación del Servicio\n" +
                        "CineFile esta enfocado a que los usuarios interaccionen de forma pacifica, cualquier vocabulario u otra forma de acciones que inciten al odia, podemos eliminar su comentario.\n" +
                        "\n" +
                        "12. Ley Aplicable y Resolución de Disputas\n" +
                        "Estos Términos se regirán e interpretarán de acuerdo con las leyes de El salvador Cualquier disputa que surja en relación con estos Términos se resolverá exclusivamente en los tribunales de [Jurisdicción].\n" +
                        "\n" +
                        "13. Contacto\n" +
                        "Por el momento no contamos con servicio de contacto.\n" +
                        "\n" +
                        "CineFile\n" +
                        "\n" +
                        "Al utilizar CineFile, usted reconoce haber leído y comprendido estos Términos de Uso y acepta cumplir con ellos.\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Políticas de privacidad
        Text(
            text = "Políticas de privacidad",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable {
                    expandedPrivacidad = !expandedPrivacidad // Cambiar el estado al hacer clic
                }
        )

        if (expandedPrivacidad) {
            Text(
                text = "Política de Privacidad de CineFile\n" +
                        "Fecha de Vigencia: 01/06/2024\n" +
                        "\n" +
                        "En CineFile, nos comprometemos a proteger su privacidad. Esta Política de Privacidad describe cómo recopilamos, usamos, compartimos y protegemos su información personal cuando utiliza nuestra aplicación móvil para calificar, comentar, responder comentarios, buscar y guardar películas. Al utilizar nuestra aplicación, usted acepta las prácticas descritas en esta Política de Privacidad.\n" +
                        "\n" +
                        "1. Información que Recopilamos\n" +
                        "a. Información que Usted Nos Proporciona\n" +
                        "Registro y Perfil: Cuando crea una cuenta en nuestra aplicación, recopilamos su nombre, dirección de correo electrónico, nombre de usuario y contraseña.\n" +
                        "Comentarios y Calificaciones: Cuando califica o comenta sobre una película, recopilamos esa información.\n" +
                        "Interacciones: Cuando responde a comentarios o interactúa con otros usuarios, recopilamos esas interacciones.\n" +
                        "b. Información Recopilada Automáticamente\n" +
                        "Datos de Uso: Recopilamos información sobre cómo utiliza la aplicación, incluyendo las páginas y funciones que visita, el tiempo que pasa en la aplicación, y las películas que busca o guarda.\n" +
                        "Datos del Dispositivo: Recopilamos información sobre el dispositivo que utiliza para acceder a nuestra aplicación, incluyendo el modelo del dispositivo, sistema operativo, identificadores únicos de dispositivo, dirección IP y datos de la red móvil.\n" +
                        "c. Información de Terceros\n" +
                        "Podemos recibir información sobre usted de fuentes de terceros y combinarla con la información que recopilamos a través de nuestra aplicación.\n" +
                        "\n" +
                        "2. Cómo Usamos su Información\n" +
                        "Utilizamos la información que recopilamos para:\n" +
                        "\n" +
                        "Proporcionar y mejorar nuestra aplicación.\n" +
                        "Personalizar su experiencia.\n" +
                        "Comunicarnos con usted sobre su cuenta o transacciones.\n" +
                        "Responder a sus comentarios y preguntas.\n" +
                        "Analizar el uso de la aplicación y las tendencias.\n" +
                        "Prevenir actividades fraudulentas o ilegales.\n" +
                        "3. Cómo Compartimos su Información\n" +
                        "Podemos compartir su información con terceros en las siguientes circunstancias:\n" +
                        "\n" +
                        "Proveedores de Servicios: Con proveedores de servicios que nos ayudan a operar y mejorar nuestra aplicación.\n" +
                        "Cumplimiento Legal: Cuando creemos que es necesario para cumplir con la ley, reglamentos, o procesos legales.\n" +
                        "Protección de Derechos: Para proteger nuestros derechos, privacidad, seguridad, propiedad, y los de nuestros usuarios y el público.\n" +
                        "Transferencias Comerciales: En relación con una fusión, adquisición, o venta de la totalidad o parte de nuestros activos.\n" +
                        "4. Seguridad de su Información\n" +
                        "Implementamos medidas de seguridad razonables para proteger la información personal contra pérdida, robo, uso indebido, acceso no autorizado, divulgación, alteración y destrucción. Sin embargo, ninguna transmisión de datos por Internet o sistema de almacenamiento es completamente seguro, por lo que no podemos garantizar la seguridad absoluta.\n" +
                        "\n" +
                        "5. Sus Derechos y Opciones\n" +
                        "Usted tiene ciertos derechos y opciones con respecto a su información personal:\n" +
                        "\n" +
                        "Acceso y Corrección: Puede acceder y actualizar su información de perfil en cualquier momento a través de la configuración de su cuenta.\n" +
                        "Eliminación: Puede solicitar la eliminación de su cuenta y de la información personal asociada.\n" +
                        "Oposición y Restricción: Puede oponerse o solicitar la restricción del procesamiento de su información personal en ciertas circunstancias.\n" +
                        "6. Retención de Datos\n" +
                        "Conservamos su información personal durante el tiempo que sea necesario para cumplir con los fines descritos en esta Política de Privacidad, a menos que se requiera o permita un período de retención más largo por ley.\n" +
                        "\n" +
                        "7. Privacidad de los Menores\n" +
                        "Nuestra aplicación no está dirigida a menores de 13 años. No recopilamos a sabiendas información personal de menores de 13 años. Si descubrimos que un menor de 13 años nos ha proporcionado información personal, tomaremos medidas para eliminar esa información.\n" +
                        "\n" +
                        "8. Cambios en esta Política de Privacidad\n" +
                        "Podemos actualizar esta Política de Privacidad periódicamente. Le notificaremos sobre cualquier cambio publicando la nueva política en nuestra aplicación. Le recomendamos que revise esta Política de Privacidad regularmente para estar informado sobre cómo protegemos su información.\n" +
                        "\n" +
                        "9. Contacto\n" +
                        "Por el momento no tenemos ningun medio de contacto\n" +
                        "\n" +
                        "CineFile\n" +

                        "\n" +
                        "Al utilizar CineFile, usted reconoce haber leído y comprendido esta Política de Privacidad y acepta nuestras prácticas de recopilación, uso y compartición de su información personal.",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

