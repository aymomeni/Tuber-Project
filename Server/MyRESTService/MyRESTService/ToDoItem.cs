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

    public class MakeTutorAvailableResponseItem
    {

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

    public class FindAvailableTutorResponseItem
    {
        [DataMember]
        public List<AvailableTutorUserItem> availableTutors { get; set; }
    }

    public class DeleteTutorUserItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }
    }

    public class DeleteTutorResponseItem
    {
       
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
    }

    // Sent to server to see if the tutor has been paired with a student for immediate tutor case
    public class CheckPairedStatusItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string latitude { get; set; }

        [DataMember]
        public string longitude { get; set; }
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
        public double studentLatitude { get; set; }

        [DataMember]
        public double studentLongitude { get; set; }

        [DataMember]
        public double tutorLatitude { get; set; }

        [DataMember]
        public double tutorLongitude { get; set; }

        [DataMember]
        public double distanceFromStudent { get; set; }
    }

    public class UpdateStudentLocationRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string latitude { get; set; }

        [DataMember]
        public string longitude { get; set; }
    }

    public class UpdateStudentLocationResponseItem
    {
        [DataMember]
        public string tutorEmail { get; set; }

        [DataMember]
        public string tutorLatitude { get; set; }

        [DataMember]
        public string tutorLongitude { get; set; }
    }

    public class UpdateTutorLocationRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string latitude { get; set; }

        [DataMember]
        public string longitude { get; set; }
    }

    public class UpdateTutorLocationResponseItem
    {
        [DataMember]
        public string studentEmail { get; set; }

        [DataMember]
        public string studentLatitude { get; set; }

        [DataMember]
        public string studentLongitude { get; set; }
    }

    public class StartTutorSessionItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }
    }

    public class StartTutorSessionResponseItem
    {
        
    }

    public class EndTutorSessionRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }
    }

    public class EndTutorSessionResponseItem
    {
        [DataMember]
        public int tutorSessionID { get; set; }

        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string studentEmail { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string sessionStartTime { get; set; }

        [DataMember]
        public string sessionEndTime { get; set; }

        [DataMember]
        public double sessionCost { get; set; }
    }

    public class RateTutorItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string tutorSessionID { get; set; }

        [DataMember]
        public string tutorEmail { get; set; }

        [DataMember]
        public string rating { get; set; }
    }

    public class RateTutorResponseItem
    {
        
    }

    public class RateStudentItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string tutorSessionID { get; set; }

        [DataMember]
        public string studentEmail { get; set; }

        [DataMember]
        public string rating { get; set; }
    }

    public class RateStudentResponseItem
    {
        
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

    public class CreateStudyHotspotResponseItem
    {
        [DataMember]
        public string hotspotID { get; set; }
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

    public class FindStudyHotspotReturnItem
    {
        [DataMember]
        public List<AvailableStudyHotspotItem> studyHotspots { get; set; }
    }

    public class StudyHotspotJoinItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string hotspotID { get; set; }
    }

    public class StudyHotspotJoinResponseItem
    {
        
    }

    public class StudyHotspotLeaveItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }
    }

    public class StudyHotspotLeaveRequestItem
    {
        
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

    public class StudyHotspotResponseItem
    {
        [DataMember]
        public List<StudyHotspotMemberItem> hotspotMembers { get; set; }
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

    public class StudyHotspotDeleteResponseItem
    {
        
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
        public string dateTime { get; set; }

        [DataMember]
        public string duration { get; set; }
    }

    public class ScheduleTutorResponseItem
    {
        
    }

    public class FindAllScheduleTutorRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }
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
        public string dateTime { get; set; }

        [DataMember]
        public string duration { get; set; }
    }

    public class FindAllScheduleTutorResponseItem
    {
        [DataMember]
        public List<ScheduleTutorRequestItem> tutorRequestItems { get; set; }
    }

        public class AcceptStudentScheduleRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string studentEmail { get; set; }

        [DataMember]
        public string course { get; set; }
    }

    public class AcceptStudentScheduleRequestResponseItem
    {
        [DataMember]
        public string student_email { get; set; }

        [DataMember]
        public string tutor_email { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string topic { get; set; }

        [DataMember]
        public string dateTime { get; set; }

        [DataMember]
        public string duration { get; set; }
    }

    public class PairedScheduledStatusItem
    {
        [DataMember]
        public string studentEmail { get; set; }

        [DataMember]
        public string tutorEmail { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string topic { get; set; }

        [DataMember]
        public string dateTime { get; set; }

        [DataMember]
        public string duration { get; set; }

        [DataMember]
        public Boolean isPaired { get; set; }
    }

    public class CheckPairedStatusResponseItem
    {
        [DataMember]
        public List<PairedScheduledStatusItem> requests { get; set; }
    }

    public class FindAllScheduleTutorAcceptedRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }
    }

    public class FindAllScheduleTutorAcceptedItem
    {
        [DataMember]
        public string studentEmail { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string topic { get; set; }

        [DataMember]
        public string dateTime { get; set; }

        [DataMember]
        public string duration { get; set; }
    }

    public class FindAllScheduleTutorAcceptedResponsetItem
    {
        [DataMember]
        public List<FindAllScheduleTutorAcceptedItem> tutorRequestItems { get; set; }
    }

    public class StartScheduledTutorSessionItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string dateTime { get; set; }
    }

    public class StartScheduledTutorSessionResponseItem
    {
        
    }

    public class ReportTutorGetTutorListRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }
    }

    public class ReportTutorGetTutorListItem
    {
        [DataMember]
        public string tutorEmail { get; set; }

        [DataMember]
        public string tutorFirstName { get; set; }

        [DataMember]
        public string tutorLastName { get; set; }
    }

    public class ReportTutorGetTutorListResponseItem
    {
        [DataMember]
        public List<ReportTutorGetTutorListItem> tutorList { get; set; }
    }

    public class ReportTutorGetSessionListRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string tutorEmail { get; set; }

        [DataMember]
        public string tutorFirstName { get; set; }

        [DataMember]
        public string tutorLastName { get; set; }
    }

    public class ReportTutorGetSessionListItem
    {
        [DataMember]
        public string tutorEmail { get; set; }

        [DataMember]
        public string tutorFirstName { get; set; }

        [DataMember]
        public string tutorLastName { get; set; }

        [DataMember]
        public string tutorSessionID { get; set; }

        [DataMember]
        public string course { get; set; }

        [DataMember]
        public string sessionStartTime { get; set; }

        [DataMember]
        public string sessionEndTime { get; set; }

        [DataMember]
        public string sessionCost { get; set; }
    }

    public class ReportTutorGetSessionListResponseItem
    {
        [DataMember]
        public List<ReportTutorGetSessionListItem> tutorList { get; set; }
    }

    public class ReportTutorRequestItem
    {
        [DataMember]
        public string userEmail { get; set; }

        [DataMember]
        public string userToken { get; set; }

        [DataMember]
        public string tutorEmail { get; set; }

        [DataMember]
        public string tutorSessionID { get; set; }

        [DataMember]
        public string message { get; set; }
    }

    public class ReportTutorResponseItem
    {
        
    }
}

