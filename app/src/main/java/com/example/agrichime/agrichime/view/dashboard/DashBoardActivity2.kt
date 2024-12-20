package com.example.agrichime.agrichime.view.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.agrichime.R
import com.example.agrichime.agrichime.view.apmc.ApmcFragment
import com.example.agrichime.agrichime.view.articles.ArticleListFragment
import com.example.agrichime.agrichime.view.articles.FruitsFragment
import com.example.agrichime.agrichime.view.auth.LoginActivity2
import com.example.agrichime.agrichime.view.ecommerce.CartFragment
import com.example.agrichime.agrichime.view.ecommerce.EcommerceItemFragment
import com.example.agrichime.agrichime.view.ecommerce.MyOrdersFragment
import com.example.agrichime.agrichime.view.ecommerce.PaymentFragment
import com.example.agrichime.agrichime.view.introscreen.IntroActivity
import com.example.agrichime.agrichime.view.socialmedia.SMCreatePostFragment
import com.example.agrichime.agrichime.view.socialmedia.SocialMediaPostsFragment
import com.example.agrichime.agrichime.view.user.UserFragment
import com.example.agrichime.agrichime.view.weather.WeatherFragment
import com.example.agrichime.agrichime.viewmodel.EcommerceFragment
import com.example.agrichime.agrichime.viewmodel.UserDataViewModel
import com.example.agrichime.agrichime.viewmodel.UserProfilePostsViewModel
import com.example.agrichime.agrichime.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Locale


class DashBoardActivity2 : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener  {


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var bottomNav: BottomNavigationView
    private  var cityTT: String=" "


    private lateinit var cartFragment: CartFragment
    private lateinit var fruitsFragment: FruitsFragment
    private lateinit var paymentFragment: PaymentFragment
    private lateinit var dashboardFragment: dashboardFragment
    private  lateinit var ecommerceFragment: EcommerceFragment
    private lateinit var ecommerceItemFragment: EcommerceItemFragment
    private lateinit var weatherFragment: WeatherFragment
    private  lateinit var navController: NavController
    private  lateinit var toggle: ActionBarDrawerToggle
    private   lateinit var blankFragment1: WeatherFragment
    private   lateinit var apmcFragment: ApmcFragment
    private   lateinit var articleListFragment: ArticleListFragment
    private   lateinit var myOrdersFragment: MyOrdersFragment
    private    lateinit var userFragment: UserFragment
    private    lateinit var socialMediaPostFragment: SocialMediaPostsFragment
    private    lateinit var smCreatePostFragment: SMCreatePostFragment
    private lateinit var viewModel: UserDataViewModel
    private lateinit var viewModel2: UserProfilePostsViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private  lateinit var sharedPreferences: SharedPreferences

    val firebaseAuth = FirebaseAuth.getInstance()

    private var firstTime: Boolean? = null

    private var userName = ""




    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board2)






        bottomNav = findViewById(R.id.bottomNav)
        drawerLayout = findViewById(R.id.drawerLayout)

        navView = findViewById(R.id.navView)
        val headerView = navView.getHeaderView(0)

        navView.setNavigationItemSelectedListener(this)



        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)




        supportActionBar?.title = "AGRICHIME"



        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check and request location permissions
        checkAndRequestPermissions()


        val cityTextNavHeader = headerView.findViewById<TextView>(R.id.cityTextNavHeader)
        val navbarUserName = headerView.findViewById<TextView>(R.id.navbarUserName)
        val navbarUserEmail = headerView.findViewById<TextView>(R.id.navbarUserEmail)
        val navbarUserImage = headerView.findViewById<ImageView>(R.id.navbarUserImage)
        val navBarUserPostCount = headerView.findViewById<TextView>(R.id.navBarUserPostCount)



        viewModel = ViewModelProvider(this)[UserDataViewModel::class.java]






        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        viewModel2 = ViewModelProvider(this)[UserProfilePostsViewModel::class.java]

        val currentUser = firebaseAuth.currentUser



        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firstTime = sharedPreferences.getBoolean("firstTime", true);



        if (firstTime!!) {
            Intent(this, IntroActivity::class.java).also {
                startActivity(it)
            }
            val editor = sharedPreferences.edit()
            firstTime = false;
            editor.putBoolean("firstTime", firstTime!!)
            editor.apply()
            finish()
            return
        } else {
            if (currentUser == null) {
                Intent(this, LoginActivity2::class.java).also {
                    startActivity(it)
                }
                finish()
                return
            } else {

            }
        }





        viewModel.getUserData(firebaseAuth.currentUser!!.email as String)





        ecommerceItemFragment = EcommerceItemFragment()
        dashboardFragment = dashboardFragment()
        weatherFragment = WeatherFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, dashboardFragment, "dashFrag")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setReorderingAllowed(true)
            .commit()



        bottomNav.selectedItemId = R.id.bottomNavHome

        val something = navView.getHeaderView(0);

        if (dashboardFragment.isVisible) {
            bottomNav.selectedItemId = R.id.bottomNavHome
        }

        something.setOnClickListener {
            Toast.makeText(this, "You Clicked Slider", Toast.LENGTH_LONG).show()

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
            userFragment = UserFragment()
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout, userFragment, "userFrag")
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                setReorderingAllowed(true)
                addToBackStack("userFrag")
                commit()
            }
        }

        apmcFragment = ApmcFragment()
        socialMediaPostFragment = SocialMediaPostsFragment()
        ecommerceFragment=EcommerceFragment()
        paymentFragment = PaymentFragment()
        cartFragment = CartFragment()
        myOrdersFragment = MyOrdersFragment()

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottomNavAPMC -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_layout, apmcFragment, "apmcFrag")
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        setReorderingAllowed(true)
                        addToBackStack("apmcFrag")
                        commit()
                    }


                }


                R.id.bottomNavHome -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_layout, dashboardFragment, "dashFrag")
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        setReorderingAllowed(true)
                        addToBackStack("dashFrag")
                        commit()
                    }
                }
                R.id.bottomNavEcomm -> {
                    fruitsFragment = FruitsFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_layout, fruitsFragment, "ecommItemFrag")
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        setReorderingAllowed(true)
                        addToBackStack("ecommItemFrag")
                        commit()
                    }
                }

                R.id.bottomNavPost -> {
                    socialMediaPostFragment = SocialMediaPostsFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_layout, socialMediaPostFragment, "socialFrag")
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        setReorderingAllowed(true)
                        addToBackStack("socialFrag")
                        commit()
                    }
                }
            }
            true
        }

        viewModel.userliveData.observe(this, Observer {


           // val posts = it.get("posts") as List<*>
            val city = it.get("city")
            userName = it.get("name").toString()

            //val allPosts = viewModel2.liveData3.value as ArrayList<DocumentSnapshot>

            if(city == null){
                cityTextNavHeader.text ="City: $cityTT"
            } else{
                cityTextNavHeader.text ="City: " +  it.get("city").toString()
            }
            val emailMM = firebaseAuth.currentUser!!.email
            val usernameMM = emailMM?.split("@")?.get(0)

            val capitalizedUsernameMM = usernameMM?.capitalize()
            navbarUserName.text = capitalizedUsernameMM
            navbarUserEmail.text = firebaseAuth.currentUser!!.email


            Glide.with(this).load(R.drawable.farmerimg).into(navbarUserImage)

            it.getString("name")?.let { it1 -> Log.d("User Data from VM", it1) }

            navBarUserPostCount.text = "Posts Count: 10"
        })
//
//



    }



    private fun checkAndRequestPermissions() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted, proceed with getting location
                getCurrentLocation()
            }
            else -> {
                // Permission is not granted, request permission
                permissionLauncher.launch(permission)
            }
        }
    }

    // Permission request result handler
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }


        fusedLocationClient.lastLocation
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    if (location != null) {
                        // Access the latitude and longitude properties
                        val latitude = location.latitude
                        val longitude = location.longitude
                        // Display the coordinates

                        // Create a Geocoder instance
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addressList = geocoder.getFromLocation(latitude, longitude, 1)

                        if (addressList != null) {
                            if (addressList.isNotEmpty()) {
                                // Get the first address in the list
                                val address = addressList[0]
                                val locality = address.locality

                                // Display the locality and coordinates

                                val coords = mutableListOf<String>()
                                val geocoder = Geocoder(this, Locale.getDefault())
                                val addresses: List<Address> =
                                    geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1)!!

                                coords.add(location!!.latitude.toString())
                                coords.add(location!!.longitude.toString())
                                coords.add(addresses[0].locality.toString())
                                weatherViewModel.updateCoordinates(coords)

                                cityTT=locality



//                                Toast.makeText(
//                                    this,
//                                    "Latitude: $latitude, Longitude: $longitude, Locality: $locality",
//                                    Toast.LENGTH_LONG
//                                ).show()
                            }else {
                                Toast.makeText(this, "Failed to obtain address from location", Toast.LENGTH_SHORT).show()
                            }}

                        else {
                            Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Task unsuccessful", Toast.LENGTH_SHORT).show()
                }
            })}



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        bottomNav.selectedItemId = R.id.bottomNavHome
        when (item.itemId) {

            R.id.miItem1 -> {
                var ecommerceFragment = EcommerceFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, ecommerceFragment, "ecommListFrag")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .addToBackStack("ecommListFrag")
                    .commit()
            }
            R.id.miItem2 -> {
                apmcFragment = ApmcFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, apmcFragment, "apmcFrag")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .addToBackStack("apmcFrag")
                    .commit()
            }
            R.id.miItem3 ->{
                smCreatePostFragment = SMCreatePostFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, smCreatePostFragment, "createPostFrag")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .addToBackStack("createPostFrag")
                    .commit()
            }
            R.id.miItem4 -> {
                socialMediaPostFragment = SocialMediaPostsFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, socialMediaPostFragment, "socialFrag")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .addToBackStack("socialFrag")
                    .commit()
            }
            R.id.miItem5 -> {
                weatherFragment = WeatherFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, weatherFragment, "weatherFrag")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .addToBackStack("weatherFrag")
                    .commit()
            }
            R.id.miItem6 -> {
                articleListFragment = ArticleListFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, articleListFragment, "articleListFrag")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .addToBackStack("articleListFrag")
                    .commit()
            }
            R.id.miItem7 -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, myOrdersFragment, "myOrdersFrag")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .addToBackStack("myOrdersFrag")
                    .commit()
            }
            R.id.miItem8 -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Log Out")
                    .setMessage("Do you want to logout?")
                    .setPositiveButton("Yes") { dialogInterface, i ->
                        firebaseAuth.signOut()
                        Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show()
                        Intent(this, LoginActivity2::class.java).also {
                            startActivity(it)
                        }
                    }
                    .setNegativeButton("No") { dialogInterface, i ->
                    }
                    .show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (dashboardFragment.isVisible) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {

            }
        }
    }


}
