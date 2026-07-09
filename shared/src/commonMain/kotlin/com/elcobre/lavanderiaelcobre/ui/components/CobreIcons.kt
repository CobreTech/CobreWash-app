package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Iconos construidos desde path data de Material Symbols para evitar la dependencia `material-icons-extended`. */
private fun cobreIcon(name: String, pathData: String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).addPath(
        pathData = PathParser().parsePathString(pathData).toNodes(),
        fill = SolidColor(Color.Black), // el tint del composable Icon lo sobrescribe
    ).build()

object CobreIcons {

    val Person: ImageVector = cobreIcon(
        "person",
        "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z",
    )

    val Lock: ImageVector = cobreIcon(
        "lock",
        "M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 " +
            "2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 " +
            "1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z",
    )

    val Mail: ImageVector = cobreIcon(
        "mail",
        "M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 " +
            "4-8 5-8-5V6l8 5 8-5v2z",
    )

    val Visibility: ImageVector = cobreIcon(
        "visibility",
        "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 " +
            "17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 " +
            "3-3-1.34-3-3-3z",
    )

    val VisibilityOff: ImageVector = cobreIcon(
        "visibility_off",
        "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 " +
            "0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 " +
            "12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 " +
            "9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 " +
            "0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z",
    )

    val Logout: ImageVector = cobreIcon(
        "logout",
        "M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z",
    )

    val FilterList: ImageVector = cobreIcon(
        "filter_list",
        "M10 18h4v-2h-4v2zM3 6v2h18V6H3zm3 7h12v-2H6v2z",
    )

    val Comment: ImageVector = cobreIcon(
        "comment",
        "M20 2H4c-1.1 0-1.99.9-1.99 2L2 22l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 12H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z",
    )

    val ArrowForward: ImageVector = cobreIcon(
        "arrow_forward",
        "M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z",
    )

    val ArrowBack: ImageVector = cobreIcon(
        "arrow_back",
        "M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z",
    )

    val Check: ImageVector = cobreIcon(
        "check",
        "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z",
    )

    val CheckCircle: ImageVector = cobreIcon(
        "check_circle",
        "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 " +
            "14.17l7.59-7.59L19 8l-9 9z",
    )

    val Warning: ImageVector = cobreIcon(
        "warning",
        "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z",
    )

    val ErrorOutline: ImageVector = cobreIcon(
        "error_outline",
        "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z",
    )

    val Notifications: ImageVector = cobreIcon(
        "notifications",
        "M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 " +
            "1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z",
    )

    val Schedule: ImageVector = cobreIcon(
        "schedule",
        "M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 " +
            "0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z",
    )

    val Inventory: ImageVector = cobreIcon(
        "inventory_2",
        "M20 2H4c-1 0-2 1-2 2v3.01c0 .72.43 1.34 1 1.69V20c0 1.1 1.1 2 2 2h14c.9 0 2-.9 2-2V8.7c.57-.35 " +
            "1-.97 1-1.69V4c0-1-1-2-2-2zm-5 12H9v-2h6v2zm5-7H4V4l16-.02V7z",
    )

    val History: ImageVector = cobreIcon(
        "history",
        "M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 " +
            "0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 " +
            "5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z",
    )

    val Close: ImageVector = cobreIcon(
        "close",
        "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z",
    )

    val Search: ImageVector = cobreIcon(
        "search",
        "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 " +
            "16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 " +
            "5 14 7.01 14 9.5 11.99 14 9.5 14z",
    )

    val Send: ImageVector = cobreIcon(
        "send",
        "M2.01 21L23 12 2.01 3 2 10l15 2-15 2z",
    )

    val PushPin: ImageVector = cobreIcon(
        "push_pin",
        "M16 12V4h1c.55 0 1-.45 1-1s-.45-1-1-1H7c-.55 0-1 .45-1 1s.45 1 1 1h1v8l-2 2v2h5.97v6l1 1 1-1v-6H19v-2l-3-2z",
    )

    val Add: ImageVector = cobreIcon(
        "add",
        "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z",
    )

    val Campaign: ImageVector = cobreIcon(
        "campaign",
        "M18 11v2h4v-2h-4zm-2 6.61c.96.71 2.21 1.65 3.2 2.39.4-.53.8-1.07 1.2-1.6-.99-.74-2.24-1.68-3.2-2.4-.4.54-.8 " +
            "1.08-1.2 1.61zM20.4 5.6c-.4-.53-.8-1.07-1.2-1.6-.99.74-2.24 1.68-3.2 2.4.4.53.8 1.07 1.2 1.6.96-.72 " +
            "2.21-1.65 3.2-2.4zM4 9c-1.1 0-2 .9-2 2v2c0 1.1.9 2 2 2h1v4h2v-4h1l5 3V6L8 9H4zm11.5 3c0-1.33-.58-2.53-1.5-3.35v6.69c.92-.81 " +
            "1.5-2.01 1.5-3.34z",
    )

    val Menu: ImageVector = cobreIcon(
        "menu",
        "M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z",
    )

    val QrCodeScanner: ImageVector = cobreIcon(
        "qr_code_scanner",
        "M9.5 6.5v3h-3v-3h3M11 5H5v6h6V5zm-1.5 9.5v3h-3v-3h3M11 13H5v6h6v-6zm6.5-6.5v3h-3v-3h3M19 5h-6v6h6V5zm-6 " +
            "8h1.5v1.5H13V13zm1.5 1.5H16V16h-1.5v-1.5zM16 13h1.5v1.5H16V13zm-3 3h1.5v1.5H13V16zm1.5 1.5H16V19h-1.5v-1.5zM16 " +
            "16h1.5v1.5H16V16zm1.5-1.5H19V16h-1.5v-1.5zm0 3H19V19h-1.5v-1.5zM22 7h-2V4h-3V2h5v5zm0 15v-5h-2v3h-3v2h5zM2 " +
            "22h5v-2H4v-3H2v5zM2 2v5h2V4h3V2H2z",
    )

    // ---- Iconos por etapa del proceso ----

    val Inbox: ImageVector = cobreIcon(
        "inbox",
        "M19 3H4.99c-1.11 0-1.98.9-1.98 2L3 19c0 1.1.88 2 1.99 2H19c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 " +
            "12h-4c0 1.66-1.35 3-3 3s-3-1.34-3-3H4.99V5H19v10z",
    )

    val LocalLaundry: ImageVector = cobreIcon(
        "local_laundry_service",
        "M9.17 16.83c1.56 1.56 4.1 1.56 5.66 0 1.56-1.56 1.56-4.1 0-5.66l-5.66 5.66zM18 2.01L6 2c-1.11 " +
            "0-2 .89-2 2v16c0 1.11.89 2 2 2h12c1.11 0 2-.89 2-2V4c0-1.11-.89-1.99-2-1.99zM10 4c.55 0 1 .45 " +
            "1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zM7 4c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm5 " +
            "16c-3.31 0-6-2.69-6-6s2.69-6 6-6 6 2.69 6 6-2.69 6-6 6z",
    )

    val Air: ImageVector = cobreIcon(
        "air",
        "M14.5 17c0 1.65-1.35 3-3 3s-3-1.35-3-3h2c0 .55.45 1 1 1s1-.45 1-1-.45-1-1-1H2v-2h9.5c1.65 0 3 " +
            "1.35 3 3zM19 6.5C19 4.57 17.43 3 15.5 3S12 4.57 12 6.5h2c0-.83.67-1.5 1.5-1.5s1.5.67 1.5 " +
            "1.5S16.33 8 15.5 8H2v2h13.5c1.93 0 3.5-1.57 3.5-3.5zm-.5 4.5H2v2h16.5c.83 0 1.5.67 1.5 " +
            "1.5S19.33 16 18.5 16v2c1.93 0 3.5-1.57 3.5-3.5S20.43 11 18.5 11z",
    )

    val Checkroom: ImageVector = cobreIcon(
        "checkroom",
        "M21.6 18.2L13 11.75v-.91c1.65-.49 2.8-2.17 2.43-4.05-.26-1.31-1.3-2.4-2.61-2.7C10.54 3.57 8.5 " +
            "5.3 8.5 7.5h2c0-.83.67-1.5 1.5-1.5s1.5.67 1.5 1.5c0 .84-.69 1.52-1.53 1.5-.54-.01-.97.45-.97.99v1.76L2.4 " +
            "18.2c-.77.58-.36 1.8.6 1.8h18c.96 0 1.37-1.22.6-1.8z",
    )

    val DoneAll: ImageVector = cobreIcon(
        "done_all",
        "M18 7l-1.41-1.41-6.34 6.34 1.41 1.41L18 7zm4.24-1.41L11.66 16.17 7.48 12l-1.41 1.41L11.66 " +
            "19l12-12-1.42-1.41zM.41 13.41L6 19l1.41-1.41L1.83 12 .41 13.41z",
    )

    val PhotoCamera: ImageVector = cobreIcon(
        "photo_camera",
        "M9 2L7.17 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2h-3.17L15 2H9zm3 " +
            "15c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 " +
            "3-3-1.34-3-3-3z",
    )

    val Speed: ImageVector = cobreIcon(
        "speed",
        "M20.38 8.57l-1.23 1.85a8 8 0 0 1-.22 7.58H5.07A8 8 0 0 1 15.58 6.85l1.85-1.23A10 10 0 0 0 3.35 " +
            "19a2 2 0 0 0 1.72 1h13.85a2 2 0 0 0 1.74-1 10 10 0 0 0-.27-10.44zM10.59 15.41a2 2 0 0 0 2.83 " +
            "0l5.66-8.49-8.49 5.66a2 2 0 0 0 0 2.83z",
    )

    val LocalShipping: ImageVector = cobreIcon(
        "local_shipping",
        "M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4zM6 " +
            "18.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM19.5 9.5l1.96 " +
            "2.5H17V9.5h2.5zM18 18.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z",
    )
}
