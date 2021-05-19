package me.foolishchow.autoparamdemo.fake

/**
 * Description:
 * Author: foolishchow
 * Date: 26/4/2021 8:49 PM
 */

//@Suppress("unchecked_cast")
//fun getNavigator(controller: NavController): Navigator<out NavGraph?> {
//    return controller.navigatorProvider.getNavigator<Navigator<*>>("navigation") as Navigator<out NavGraph?>
//}
//
//object NavigationUtils {
//    @JvmStatic
//    fun createNavigator(controller: NavController): NavGraph {
//        val navigator: Navigator<*> = controller.navigatorProvider.getNavigator("navigation")
//        return navigator.createDestination() as NavGraph
//    }
//
//    @JvmStatic
//    fun createDestination(controller: NavController): FragmentNavigator.Destination {
//        val navigator: FragmentNavigator = controller.navigatorProvider.getNavigator(FragmentNavigator::class.java)
//        return navigator.createDestination()
//    }
//
//}