package com.ebenezer.gana.shoppy.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.ebenezer.gana.shoppy.models.*
import com.ebenezer.gana.shoppy.ui.activities.*
import com.ebenezer.gana.shoppy.ui.fragments.DashboardFragment
import com.ebenezer.gana.shoppy.ui.fragments.OrdersFragment
import com.ebenezer.gana.shoppy.ui.fragments.ProductsFragment
import com.ebenezer.gana.shoppy.ui.fragments.SoldProductsFragment
import com.ebenezer.gana.shoppy.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

/**
 * A custom class where we will add the operation performed for the FireStore database.
 */
class FirestoreClass {

    // Access a Cloud Firestore instance.
    private val mFirestore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.

        mFirestore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)

            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())

            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }


            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user",
                    e
                )
            }
    }

    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun getCurrentUserId(): String {
        // An instance of currentUser using FirebaseAuth module
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank
        /* var currentUserID = ""
         currentUserID.let {
             currentUserID = currentUser!!.uid
         }*/
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    /**
     * A function to get the user id of current logged user.
     */
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFirestore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.MYSHOPPAL_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()


                when (activity) {
                    //check the type of activity
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)

                    }
                }

            }

            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }


    /**
     * A function to update the user profile data into the database.
     *
     * @param activity The activity is used for identifying the Base activity to which the result is passed.
     * @param userHashMap HashMap of fields which are to be updated.
     */
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        //hide the progress dialog, show a toast and send the user to the
                        // main activity
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog when there is any error
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details",
                    e
                )
            }

    }

    // A function to upload the image to the cloud storage.

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "$imageType ${System.currentTimeMillis()}.${
                Constants.getFileExtension(
                    activity, imageFileURI
                )
            }"
        )

        // upload the file to the cloud
        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            // The image upload is success
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            // Get the downloadable url from the task snapshot

            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }

        }
            .addOnFailureListener { exception ->

                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }


    /**
     * A function to update all the required details in the cloud firestore after placing the order successfully.
     *
     * @param activity Base class.
     * @param cartList List of cart items.
     */
    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {
        val writeBatch = mFirestore.batch()


        // Prepare the sold product details

        for (cartItem in cartList) {
            val soldProducts = SoldProduct(
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address,

            )

            val documentReference =
                mFirestore.collection(Constants.SOLD_PRODUCTS)
                    .document(cartItem.product_id)

            writeBatch.set(documentReference, soldProducts)
        }

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cartItem in cartList) {

            /* //val productHashMap = HashMap<String, Any>()

           *//* productHashMap[Constants.STOCK_QUANTITY] =
                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()*/

            val productHashMap = HashMap<String, Any>()


            productHashMap[Constants.STOCK_QUANTITY] =
                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()


            val documentReference = mFirestore.collection(Constants.PRODUCTS)
                .document(cartItem.product_id)
            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cartItem in cartList) {
            val documentReference = mFirestore.collection(Constants.CART_ITEMS)
                .document(cartItem.id)
            writeBatch.delete(documentReference)

        }

        writeBatch.commit()
            .addOnSuccessListener {
                activity.allDetailsUpdatedSuccess()
            }


            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating all details", it
                )
            }


    }

    /**
     * A function to get the list of sold products from the cloud firestore.
     *
     *  @param fragment Base class
     */
    fun getSoldProductsList(fragment: SoldProductsFragment) {
        mFirestore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {

                val soldProductsList = ArrayList<SoldProduct>()
                for (items in it.documents) {
                    val soldProduct = items.toObject(SoldProduct::class.java)!!
                    soldProduct.id = items.id

                    soldProductsList.add(soldProduct)
                }

                fragment.successSoldProductsList(soldProductsList)


            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error getting sold products list", it
                )
            }

    }

    fun deleteASoldProduct(fragment: SoldProductsFragment, userId: String) {
        mFirestore.collection(Constants.SOLD_PRODUCTS)
            .document(userId)
            .delete()
            .addOnSuccessListener {
                fragment.successDeletingASoldProduct()
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while deleting all orders", it
                )
            }

    }

    fun deleteAllOrders(fragment: OrdersFragment, userId: String) {
        mFirestore.collection(Constants.ORDERS)
            .document(userId)
            .delete()
            .addOnSuccessListener {
                fragment.successDeleteAllOrders()
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while deleting all orders", it
                )
            }

    }


    /**
     * A function to get the list of orders from cloud firestore.
     */
    fun getMyOrdersList(fragment: OrdersFragment) {
        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val ordersList: ArrayList<Order> = ArrayList()


                for (items in it.documents) {
                    val orderItem = items.toObject(Order::class.java)!!
                    orderItem.id = items.id
                    ordersList.add(orderItem)
                }

                fragment.populateOrdersListInUI(ordersList)

            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while placing an order", it
                )
            }


    }

    /**
     * A function to place an order of the user in the cloud firestore.
     *
     * @param activity base class
     * @param order Order Info
     */
    fun placeOrder(activity: CheckoutActivity, order: Order) {
        mFirestore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()

            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order", it
                )
            }


    }

    /**
     * A function to make an entry of the user's product in the cloud firestore database.
     */
    fun uploadProductDetails(activity: AddProductActivity, productInfo: Products) {

        mFirestore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {

                // We call the AddProductActivity to handle the upload success
                activity.productUploadSuccess()
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details", exception
                )
            }
    }

    //Gets the product list for a single user from Firestore
    fun getProductsList(fragment: Fragment) {
        mFirestore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Products> = ArrayList()
                for (item in document.documents) {

                    val product = item.toObject(Products::class.java)
                    product!!.product_id = item.id

                    productsList.add(product)

                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }

                }
            }
            .addOnFailureListener { exception ->
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.hideProgressDialog()
                        Log.e(
                            fragment.javaClass.simpleName,
                            "Something went wrong, couldn't get product details", exception
                        )

                    }

                }

            }

    }

    /**
     * A function to get the product details based on the product id.
     */
    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        // The collection name for PRODUCTS

        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener {
                val product = it.toObject(Products::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Something went wrong, couldn't get product details", it
                )
            }
    }

    /**
     * A function to add the item to the cart in the cloud firestore.
     *
     * @param activity
     * @param addToCart
     */
    // function to actually add cart items to the cloud as collection
    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }

            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the product",
                    it
                )
            }

    }

    /**
     * A function to delete the product from the cloud firestore.
     */
    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product",
                    it
                )
            }

    }

    /**
     * A function to get the cart items list from the cloud firestore.
     *
     * @param activity
     */
    fun getCartList(activity: Activity) {
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, it.documents.toString())
                val cartList: ArrayList<CartItem> = ArrayList()

                for (items in it.documents) {
                    val cartItem = items.toObject(CartItem::class.java)!!
                    cartItem.id = items.id

                    cartList.add(cartItem)

                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(cartList)

                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(cartList)
                    }
                }

            }
            .addOnFailureListener {
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()

                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while trying to get cart list from firestore",
                    it
                )
            }
    }

    /**
     * A function to delete the existing address from the cloud firestore.
     *
     * @param activity Base class
     * @param addressId existing address id
     */
    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while trying to get cart list from firestore",
                    it
                )
            }

    }


    /**
     * A function to update the existing address to the cloud firestore.
     *
     * @param activity Base class
     * @param addressInfo Which fields are to be updated.
     * @param addressId existing address id
     */
    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)

            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()

            }
            .addOnFailureListener {
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while trying to get cart list from firestore",
                    it
                )
            }
    }


    /**
     * A function to get the list of address from the cloud firestore.
     *
     * @param activity
     */
    fun getAddressesList(activity: AddressListActivity) {
        mFirestore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val addressList: ArrayList<Address> = ArrayList()

                for (address in it.documents) {
                    val addressItem = address.toObject(Address::class.java)!!
                    addressItem.id = address.id

                    addressList.add(addressItem)

                }
                activity.successAddressListFromFirestore(addressList = addressList)


            }
            .addOnFailureListener {
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting address list from firestore",
                    it
                )

            }
    }

    /**
     * A function to add address to the cloud firestore.
     *
     * @param activity
     * @param addressInfo
     */
    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFirestore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener {

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating item from the cart list",
                    it
                )

            }

    }

    /**
     * A function to update the cart item in the cloud firestore.
     *
     * @param activity activity class.
     * @param id cart id of the item.
     * @param itemHashMap to be updated values.
     */
    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener {
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating item from the cart list",
                    it
                )

            }


    }

    /**
     * A function to check whether the item already exist in the cart or not.
     */
    fun checkIfItemExistsInCart(activity: ProductDetailsActivity, productId: String) {
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener {
                if (it.documents.size > 0) {
                    activity.productExistInCart()
                } else {
                    activity.hideProgressDialog()
                }

            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking if the item exist",
                    it
                )

            }
    }


    /**
     * A function to remove the cart item from the cloud firestore.
     *
     * @param activity activity class.
     * @param cart_id cart id of the item.
     */
    fun removedItemFromCart(context: Context, cart_id: String) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {

                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing item from the cart list",
                    it
                )

            }
    }

    /**
     * A function to get all the product list from the cloud firestore.
     *
     * @param activity The activity is passed as parameter to the function because it is called from activity and need to the success result.
     */
    fun getAllProductsList(activity: Activity) {
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {
                Log.e("Products List", it.documents.toString())
                val allProductsList = ArrayList<Products>()
                for (items in it.documents) {
                    val product = items.toObject(Products::class.java)!!
                    product.product_id = items.id

                    allProductsList.add(product)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successProductsListFromFireStore(allProductsList)

                    }
                    is CheckoutActivity -> {
                        activity.successProductsListsFromFireStore(productsList = allProductsList)
                    }
                }


            }
            .addOnFailureListener {
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()

                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()

                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details",
                    it
                )
            }
    }


    /**
     * A function to get the dashboard items list. The list will be an overall items list, not based on the user's id.
     */
    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.toString())

                val productList: ArrayList<Products> = ArrayList()
                for (item in document.documents) {
                    val allProducts = item.toObject(Products::class.java)!!
                    allProducts.product_id = item.id

                    productList.add(allProducts)
                }
                fragment.successDashboardItemsList(productList)

            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error getting item list", it)
            }
    }


}