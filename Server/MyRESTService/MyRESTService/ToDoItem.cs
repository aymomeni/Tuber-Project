using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using System.ServiceModel;
using System.Runtime.Serialization;


namespace ToDoList
{
    public class UserItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userPassword { get; set; }

        //[DataMember]
        //public ArrayList userCourses { get; set; }

        //[DataMember]
        //public string UserToken { get; set; }
    }

    public class VerifiedUserItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userPassword { get; set; }

        [DataMember]
        public ArrayList userStudentCourses { get; set; }

        [DataMember]
        public ArrayList userTutorCourses { get; set; }

        [DataMember]
        public string userToken { get; set; }
    }

    public class MakeUserItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userPassword { get; set; }

    }

    public class TutorUserItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string tutorCourse { get; set; }

        [DataMember]
        public string latitude { get; set; }

        [DataMember]
        public string longitude { get; set; }
    }

    public class AvailableTutorUserItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string tutorCourse { get; set; }

        [DataMember]
        public double latitude { get; set; }

        [DataMember]
        public double longitude { get; set; }

        [DataMember]
        public double distanceFromStudent { get; set; }
    }

    public class DeleteTutorUserItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }
    }


































    [DataContract]
    public class Product
    {
        [DataMember]
        public int ProductId { get; set; }
        [DataMember]
        public string Name { get; set; }
        [DataMember]
        public string CategoryName { get; set; }
        [DataMember]
        public int Price { get; set; }
    }


    public partial class Products
    {
       private static readonly Products _instance = new Products();
       
       private Products() { } 
       
       public static Products Instance 
       { 
                get { return _instance; } 
       } 
        public List<Product> ProductList 
        { 
               get { return products; } 
        } 
        private List<Product> products = new List<Product>() 
        { 
                new Product() { ProductId = 1, Name = "Product 1", CategoryName = "Category 1", Price=10}, 
                new Product() { ProductId = 1, Name = "Product 2", CategoryName = "Category 2", Price=5}, 
                new Product() { ProductId = 1, Name = "Product 3", CategoryName = "Category 3", Price=15}, 
                new Product() { ProductId = 1, Name = "Product 4", CategoryName = "Category 1", Price=9} 
        }; 
    }
}

