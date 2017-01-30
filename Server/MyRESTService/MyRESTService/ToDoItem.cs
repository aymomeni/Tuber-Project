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

    public class CreateUserItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userPassword { get; set; }

        [DataMember]
        public string userFirstName { get; set; }

        [DataMember]
        public string userLastName { get; set; }

        [DataMember]
        public string userBillingAddress { get; set; }

        [DataMember]
        public string userBillingCity { get; set; }

        [DataMember]
        public string userBillingState { get; set; }

        [DataMember]
        public string userBillingCCNumber { get; set; }

        [DataMember]
        public string userBillingCCExpDate { get; set; }

        [DataMember]
        public string userBillingCCV { get; set; }
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

    public class StudentTutorRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string requestedTutorEmail { get; set; }

        [DataMember]
        public string studentLatitude { get; set; }

        [DataMember]
        public string studentLongitude { get; set; }
    }

    public class StudentTutorPairedItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string requestedTutorEmail { get; set; }

        [DataMember]
        public string tutorCourse { get; set; }

        [DataMember]
        public string studentLatitude { get; set; }

        [DataMember]
        public string studentLongitude { get; set; }

        [DataMember]
        public string tutorLatitude { get; set; }

        [DataMember]
        public string tutorLongitude { get; set; }

        [DataMember]
        public int session_status { get; set; }
    }

    // Sent to server to see if the tutor has been paired with a student for immediate tutor case
    public class CheckPairedStatusItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }
    }

    public class PairedStatusItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string studentEmail { get; set; }

        [DataMember]
        public string tutorCourse { get; set; }

        [DataMember]
        public string studentLatitude { get; set; }

        [DataMember]
        public string studentLongitude { get; set; }

        [DataMember]
        public string tutorLatitude { get; set; }

        [DataMember]
        public string tutorLongitude { get; set; }

        [DataMember]
        public int session_status { get; set; }
    }

    public class CreateStudyHotspotRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string latitude { get; set; }

        [DataMember]
        public string longitude { get; set; }
    }

    public class StudyHotspotItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string latitude { get; set; }

        [DataMember]
        public string longitude { get; set; }
    }

    public class AvailableStudyHotspotItem
    {
        [DataMember]
        public string hotspotID { get; set; }

        [DataMember]
        public string ownerEmail { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public double latitude { get; set; }

        [DataMember]
        public double longitude { get; set; }

        [DataMember]
        public string student_count { get; set; }

        [DataMember]
        public double distanceToHotspot { get; set; }
    }

    public class StudyHotspotJoinItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string hotspotID { get; set; }
    }

    public class StudyHotspotLeaveItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }
    }

    public class StudyHotspotGetMemberItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string hotspotID { get; set; }
    }

    public class StudyHotspotMemberItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string firstName { get; set; }

        [DataMember]
        public string lastName { get; set; }
    }

    public class StudyHotspotDeleteItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string hotspotID { get; set; }
    }

    public class ScheduleTutorItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string topic { get; set; }

        [DataMember]
        public string date { get; set; }

        [DataMember]
        public string time { get; set; }

        [DataMember]
        public string duration { get; set; }
    }

    public class FindAllScheduleTutorRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }

        //[DataMember]
        //public string topic { get; set; }

        //[DataMember]
        //public string date { get; set; }

        //[DataMember]
        //public string time { get; set; }

        //[DataMember]
        //public string duration { get; set; }
    }

    public class ScheduleTutorRequestItem
    {
        [DataMember]
        public string studentEmail { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string topic { get; set; }

        [DataMember]
        public string date { get; set; }

        [DataMember]
        public string time { get; set; }

        [DataMember]
        public string duration { get; set; }
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

